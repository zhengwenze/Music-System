package org.zwz.mod_upload.util;

public class CheckUtil {

    public static Boolean isImage(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        String type = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return type.equals(".jpg") || type.equals(".jpeg") || type.equals(".png") || type.equals(".gif") || type.equals(".bmp");
    }

    public static Boolean isLyric(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        String type = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return type.equals(".lrc") || type.equals(".txt");
    }

    public static Boolean isMusic(String filename) {
        if (filename == null || filename.isEmpty()) {
            return false;
        }
        String type = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        return type.equals(".mp3") || type.equals(".wav") || type.equals(".flac") || type.equals(".m4a");
    }
}