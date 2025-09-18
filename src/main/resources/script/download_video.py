import sys
import json
from pytubefix import YouTube

def download_video(url, output_path):
    yt = YouTube(url, on_progress_callback=None)  # pas de progress bar
    stream = yt.streams.get_highest_resolution()
    out_file = stream.download(output_path=output_path)
    return out_file

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print(json.dumps({"status": "error", "message": "Missing arguments"}))
        sys.exit(1)

    video_url = sys.argv[1]
    output_dir = sys.argv[2]

    try:
        file_path = download_video(video_url, output_dir)
        print(json.dumps({"status": "ok", "path": file_path}))
    except Exception as e:
        print(json.dumps({"status": "error", "message": str(e)}))
