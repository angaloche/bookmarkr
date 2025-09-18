package com.bookstore.bookstore.controller;

import com.bookstore.bookstore.service.YoutubeDownloadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Contrôleur pour les vues Thymeleaf ET l'API REST
 */
@Controller
@RequestMapping("/youtube")
public class YoutubeController {

    private static final Logger logger = LoggerFactory.getLogger(YoutubeController.class);
    private final YoutubeDownloadService downloadService;

    @Autowired
    public YoutubeController(YoutubeDownloadService downloadService) {
        this.downloadService = downloadService;
    }

    // ===== VOS VUES THYMELEAF =====

    /**
     * Page pour entrer l'URL (VOS VUES)
     */
    @GetMapping("/form")
    public String showForm() {
        return "youtubeForm";
    }

    /**
     * Télécharge une vidéo (VOS VUES)
     */
    @PostMapping("/download")
    public String downloadVideo(@RequestParam String url, Model model) {
        try {
            logger.info("Demande de téléchargement via form pour: {}", url);

            // Utiliser le service qui retourne le chemin
            String path = downloadService.downloadVideo(url);

            model.addAttribute("status", "ok");
            model.addAttribute("path", path);
            model.addAttribute("fileName", Paths.get(path).getFileName().toString());
            model.addAttribute("message", "Téléchargement terminé !");

            logger.info("Téléchargement réussi: {}", path);

        } catch (RuntimeException e) {
            logger.error("Erreur lors du téléchargement via form", e);
            model.addAttribute("status", "error");
            model.addAttribute("message", "Erreur : " + e.getMessage());
        } catch (Exception e) {
            logger.error("Exception inattendue lors du téléchargement via form", e);
            model.addAttribute("status", "error");
            model.addAttribute("message", "Erreur inattendue : " + e.getMessage());
        }
        return "youtubeResult";
    }

    /**
     * Sert les fichiers téléchargés (POUR VOS VUES)
     */
    @GetMapping("/download-file")
    public ResponseEntity<Resource> downloadFile(@RequestParam String filename) {
        try {
            logger.info("Demande de téléchargement de fichier: {}", filename);

            Path filePath = Paths.get("downloads").resolve(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                logger.warn("Fichier non trouvé: {}", filePath);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Erreur lors du téléchargement du fichier: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== API REST OPTIONNELLE =====

    /**
     * API REST pour télécharger une vidéo
     * POST /youtube/api/download
     */
    @PostMapping(value = "/api/download", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> downloadVideoApi(@RequestBody DownloadRequest request) {
        try {
            logger.info("Demande de téléchargement API pour: {}", request.getUrl());

            // Utiliser la méthode existante et créer une réponse JSON
            String filePath = downloadService.downloadVideo(request.getUrl());

            String jsonResponse = "{\"status\":\"success\",\"downloadPath\":\"" +
                    filePath.replace("\"", "\\\"") +
                    "\",\"fileName\":\"" +
                    Paths.get(filePath).getFileName().toString().replace("\"", "\\\"") +
                    "\",\"message\":\"Téléchargement terminé avec succès\"}";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);

        } catch (RuntimeException e) {
            logger.error("Erreur lors du téléchargement API", e);
            String errorJson = "{\"status\":\"error\",\"error\":\"" +
                    e.getMessage().replace("\"", "\\\"") + "\"}";
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorJson);
        } catch (Exception e) {
            logger.error("Exception lors du téléchargement API", e);
            String errorJson = "{\"status\":\"error\",\"error\":\"Exception inattendue\"}";
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorJson);
        }
    }

    /**
     * API REST pour vérifier le statut du service
     * GET /youtube/api/health
     */
    @GetMapping(value = "/api/health", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("{\"status\":\"ok\",\"service\":\"youtube-download\"}");
    }

    /**
     * Classe pour recevoir les requêtes API JSON
     */
    public static class DownloadRequest {
        private String url;

        public DownloadRequest() {}

        public DownloadRequest(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}