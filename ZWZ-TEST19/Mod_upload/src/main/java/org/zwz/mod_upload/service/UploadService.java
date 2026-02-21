package org.zwz.mod_upload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.UUID;

@Service
public class UploadService {
    @Value("${service.download.address}")
    String address;
    @Value("${service.ipAddr}")
    String ipAddr;
    @Value("${server.port}")
    String port;

    public String uploadImage(MultipartFile file) {
        return uploadFile(file, "image");
    }

    public String uploadLyric(MultipartFile file) {
        return uploadFile(file, "lyric");
    }

    public String uploadMusic(MultipartFile file) {
        return uploadFile(file, "music");
    }

    public String uploadFile(MultipartFile file, String type) {
        OutputStream os = null;
        InputStream is = null;
        String newName = getNewName(file);
        try {
            is = file.getInputStream();
            byte[] bs = new byte[1024];
            int length;
            File tempFile = new File(address.concat(type));
            if (!tempFile.exists()) {
                tempFile.mkdirs();
            }
            os = new FileOutputStream(tempFile.getPath().concat(File.separator).concat(newName));
            while ((length = is.read(bs)) != -1) {
                os.write(bs, 0, length);
            }
            // 修改URL构建逻辑，使用配置的ipAddr属性
            return ipAddr.concat(":").concat(port).concat("/file/").concat(type).concat("/").concat(newName);
        } catch (Exception e) {
            System.out.println("文件上传异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String getNewName(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String filename = file.getOriginalFilename();
        String newName = uuid + filename.substring(filename.lastIndexOf("."));
        return newName;
    }
}