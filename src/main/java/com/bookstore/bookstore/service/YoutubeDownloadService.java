package com.bookstore.bookstore.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class YoutubeDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeDownloadService.class);
    public static final String UTF_8 = "UTF-8";
    public static final String PYTHON_3 = "python3";
    private final Path tempDir;
    private final Path downloadDir;
    private final ObjectMapper objectMapper;

    // Pattern pour valider les URLs YouTube
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
            "^(https?://)?(www\\.)?(youtube\\.com/(watch\\?v=|embed/|v/)|youtu\\.be/)([a-zA-Z0-9_-]{11})"
    );

    public YoutubeDownloadService() throws IOException {
        this.objectMapper = new ObjectMapper();

        // Créer un répertoire temporaire dédié
        this.tempDir = Files.createTempDirectory("youtube_temp");
        this.downloadDir = Paths.get("downloads");

        // Créer le répertoire de téléchargement s'il n'existe pas
        Files.createDirectories(downloadDir);

        // Afficher les chemins pour debug
        logger.info("Répertoire temporaire: {}", tempDir.toAbsolutePath());
        logger.info("Répertoire de téléchargement: {}", downloadDir.toAbsolutePath());

        // Nettoyer les anciens fichiers temporaires au démarrage
        cleanupTempFiles();
    }

    /**
     * Télécharge une vidéo YouTube depuis une URL
     *
     * @param url URL de la vidéo YouTube
     * @return Le chemin du fichier téléchargé (pour compatibilité avec le contrôleur existant)
     * @throws RuntimeException si une erreur survient
     */
    public String downloadVideo(String url) throws RuntimeException {
        if (!isValidYouTubeUrl(url)) {
            throw new RuntimeException("URL YouTube invalide");
        }

        String uniqueId = UUID.randomUUID().toString();
        Path tempScript = null;
        Process process = null;

        try {
            logger.info("Démarrage du téléchargement pour: {}", url);

            // Créer un script temporaire unique
            tempScript = tempDir.resolve("download_" + uniqueId + ".py");

            // Contenu du script Python avec pytubefix
            String pythonScript = createPythonScript(url, downloadDir.toAbsolutePath().toString());
            Files.write(tempScript, pythonScript.getBytes(UTF_8));

            // Exécuter le script
            ProcessBuilder pb = new ProcessBuilder(PYTHON_3, tempScript.toString());
            pb.redirectErrorStream(true);

            process = pb.start();

            // Lire la sortie avec timeout de 2 minutes
            String output = readProcessOutput(process, 120);
            logger.info("=== SORTIE COMPLÈTE DU SCRIPT ===");
            logger.info(output);
            logger.info("=== FIN SORTIE ===");

            int exitCode = process.waitFor();
            logger.info("Script terminé avec le code: {}", exitCode);

            if (exitCode == 0) {
                // Extraire le résultat JSON et retourner le chemin du fichier
                String jsonResult = extractJsonFromOutput(output);
                if (jsonResult != null) {
                    try {
                        JsonNode result = objectMapper.readTree(jsonResult);
                        if ("success".equals(result.get("status").asText())) {
                            String downloadPath = result.get("downloadPath").asText();
                            logger.info("Téléchargement réussi: {}", downloadPath);
                            return downloadPath; // Retourner juste le chemin pour le contrôleur
                        } else {
                            throw new RuntimeException(result.get("error").asText());
                        }
                    } catch (Exception e) {
                        logger.error("Erreur lors du parsing du JSON", e);
                        throw new RuntimeException("Erreur lors du parsing de la réponse");
                    }
                } else {
                    logger.info("Aucun JSON trouvé dans la sortie: {}", output);
                    throw new RuntimeException("Format de réponse invalide");
                }
            } else {
                logger.error("Erreur du script Python (code {}): {}", exitCode, output);
                // Extraire l'erreur du JSON si possible
                String jsonResult = extractJsonFromOutput(output);
                if (jsonResult != null) {
                    try {
                        JsonNode result = objectMapper.readTree(jsonResult);
                        throw new RuntimeException(result.get("error").asText());
                    } catch (Exception e) {
                        // Si on ne peut pas parser le JSON, utiliser la sortie brute
                        throw new RuntimeException("Erreur lors du téléchargement: " + output);
                    }
                } else {
                    throw new RuntimeException("Erreur lors du téléchargement: " + output);
                }
            }

        } catch (RuntimeException e) {
            // Re-lancer les RuntimeException sans les wrapper
            throw e;
        } catch (Exception e) {
            logger.error("Exception lors du téléchargement de {}", url, e);
            throw new RuntimeException("Exception: " + e.getMessage(), e);
        } finally {
            // Nettoyer les ressources
            cleanup(process, tempScript);
        }
    }

    /**
     * Valide si l'URL est une URL YouTube valide
     */
    private boolean isValidYouTubeUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return YOUTUBE_URL_PATTERN.matcher(url.trim()).find();
    }

    /**
     * Crée le script Python pour télécharger avec pytubefix
     */
    private String createPythonScript(String url, String downloadPath) {
        return String.format("""
                from pytubefix import YouTube
                from pytubefix.cli import on_progress
                import json
                import sys
                import os
                from pathlib import Path
                import traceback
                
                def download_video():
                    try:
                        # Créer le dossier de téléchargement
                        download_dir = Path('%s')
                        download_dir.mkdir(exist_ok=True, parents=True)
                
                        # URL à télécharger
                        url = '%s'
                
                        print(f"Démarrage du téléchargement: {url}")
                
                        # Créer l'objet YouTube avec callback de progression
                        yt = YouTube(url, on_progress_callback=on_progress)
                
                        # Récupérer les informations de la vidéo
                        title = yt.title
                        length = yt.length
                        author = yt.author
                        views = yt.views
                
                        print(f"Titre: {title}")
                        print(f"Auteur: {author}")
                        print(f"Durée: {length} secondes ({length//60}:{length%%60:02d})")
                        print(f"Vues: {views}")
                
                        # Vérifier la durée (max 1 heure)
                        if length > 3600:  # 1 heure
                            raise ValueError(f"Vidéo trop longue: {length//60} minutes (max 60 min)")
                
                        # Obtenir le stream de plus haute résolution
                        stream = yt.streams.get_highest_resolution()
                
                        if not stream:
                            # Fallback: essayer progressive mp4
                            stream = yt.streams.filter(progressive=True, file_extension='mp4').order_by('resolution').desc().first()
                
                        if not stream:
                            # Fallback ultime: premier stream disponible
                            stream = yt.streams.first()
                
                        if not stream:
                            raise ValueError("Aucun stream disponible pour cette vidéo")
                
                        print(f"Résolution: {stream.resolution}")
                        print(f"Format: {stream.mime_type}")
                        print(f"Taille: {stream.filesize_mb:.1f} MB")
                
                        # Télécharger dans le répertoire spécifié
                        print("\\nDémarrage du téléchargement...")
                        filename = stream.download(output_path=str(download_dir))
                
                        print("\\nTéléchargement terminé!")
                
                        # Vérifier que le fichier existe
                        if not os.path.exists(filename):
                            raise ValueError("Le fichier téléchargé n'existe pas")
                
                        file_size = os.path.getsize(filename)
                
                        # Retourner le résultat en JSON
                        result = {
                            "status": "success",
                            "title": title,
                            "author": author,
                            "url": url,
                            "downloadPath": filename,
                            "fileName": os.path.basename(filename),
                            "duration": length,
                            "views": views,
                            "fileSize": file_size,
                            "resolution": stream.resolution,
                            "format": stream.mime_type,
                            "message": "Téléchargement terminé avec succès"
                        }
                
                        print("\\nJSON_RESULT:" + json.dumps(result, ensure_ascii=False))
                
                    except Exception as e:
                        print(f"\\nErreur: {str(e)}")
                        print(f"Type d'erreur: {type(e).__name__}")
                        traceback.print_exc()
                
                        error_result = {
                            "status": "error",
                            "error": str(e),
                            "errorType": type(e).__name__,
                            "url": '%s'
                        }
                        print("\\nJSON_RESULT:" + json.dumps(error_result, ensure_ascii=False))
                        sys.exit(1)
                
                if __name__ == "__main__":
                    download_video()
                """, downloadPath, url, url);
    }

    /**
     * Lit la sortie du processus Python avec timeout
     */
    private String readProcessOutput(Process process, int timeoutSeconds) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"))) {
            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                throw new InterruptedException("Timeout après " + timeoutSeconds + " secondes");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\\n");
                logger.info("Python output: {}", line);
            }
        }

        return output.toString();
    }

    private String extractJsonFromOutput(String output) {
        // Chercher directement dans la chaîne complète, pas ligne par ligne
        int jsonIndex = output.indexOf("JSON_RESULT:");
        if (jsonIndex != -1) {
            String json = output.substring(jsonIndex + "JSON_RESULT:".length()).trim();

            // Prendre seulement jusqu'au prochain \n s'il y en a un
            int endIndex = json.indexOf("\n");
            if (endIndex != -1) {
                json = json.substring(0, endIndex);
            }

            try {
                // Valider que c'est un JSON valide
                objectMapper.readTree(json);
                logger.info("JSON extrait avec succès: {}", json);
                return json;
            } catch (Exception e) {
                logger.warn("JSON invalide extrait: {}", json, e);
            }
        }

        logger.warn("Aucun JSON_RESULT trouvé dans la sortie");
        return null;
    }

    /**
     * Crée une réponse d'erreur standardisée
     */
    private String createErrorResponse(String errorMessage, String url) {
        try {
            var errorResponse = objectMapper.createObjectNode();
            errorResponse.put("status", "error");
            errorResponse.put("error", errorMessage);
            errorResponse.put("url", url);
            return objectMapper.writeValueAsString(errorResponse);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la réponse d'erreur", e);
            return "{\"status\":\"error\",\"error\":\"" + errorMessage.replace("\"", "\\\"") + "\"}";
        }
    }

    /**
     * Nettoie les ressources (processus et fichiers temporaires)
     */
    private void cleanup(Process process, Path tempScript) {
        // Arrêter le processus s'il est encore en cours
        if (process != null && process.isAlive()) {
            logger.info("Arrêt forcé du processus Python");
            process.destroyForcibly();
            try {
                process.waitFor(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Supprimer le fichier temporaire
        if (tempScript != null) {
            try {
                Files.deleteIfExists(tempScript);
                logger.info("Fichier temporaire supprimé: {}", tempScript);
            } catch (IOException e) {
                logger.info("Impossible de supprimer le fichier temporaire: {}", tempScript, e);
            }
        }
    }

    /**
     * Nettoie tous les anciens fichiers temporaires
     */
    private void cleanupTempFiles() {
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .filter(path -> !path.equals(tempDir))
                        .filter(path -> path.toString().endsWith(".py"))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                                logger.info("Ancien fichier temporaire supprimé: {}", path);
                            } catch (IOException e) {
                                logger.warn("Impossible de supprimer: {}", path, e);
                            }
                        });
            }
        } catch (IOException e) {
            logger.warn("Erreur lors du nettoyage des fichiers temporaires", e);
        }
    }

    /**
     * Méthode appelée à l'arrêt de l'application
     */
    @PreDestroy
    public void onDestroy() {
        logger.info("Nettoyage final du service YouTube");
        try {
            if (Files.exists(tempDir)) {
                Files.walk(tempDir)
                        .sorted((a, b) -> b.compareTo(a)) // Supprimer les fichiers avant les dossiers
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                logger.warn("Impossible de supprimer: {}", path, e);
                            }
                        });
            }
        } catch (IOException e) {
            logger.warn("Erreur lors du nettoyage final", e);
        }
    }

    /**
     * Télécharge une vidéo YouTube et retourne les détails complets en JSON
     * (Utile si vous voulez plus d'informations dans le futur)
     *
     * @param url URL de la vidéo YouTube
     * @return JSON avec le résultat du téléchargement
     */
    public String downloadVideoWithDetails(String url) {
        try {
            String filePath = downloadVideo(url);
            return createSuccessResponse(filePath, url);
        } catch (RuntimeException e) {
            return createErrorResponse(e.getMessage(), url);
        }
    }

    private String createSuccessResponse(String filePath, String url) {
        try {
            var response = objectMapper.createObjectNode();
            response.put("status", "success");
            response.put("downloadPath", filePath);
            response.put("fileName", Paths.get(filePath).getFileName().toString());
            response.put("url", url);
            response.put("message", "Téléchargement terminé avec succès");
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la réponse de succès", e);
            return "{\"status\":\"success\",\"downloadPath\":\"" + filePath.replace("\"", "\\\"") + "\"}";
        }
    }
}