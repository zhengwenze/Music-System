package org.zwz.mod_upload.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zwz.mod_upload.entity.Mess;
import org.zwz.mod_upload.service.UploadService;
import org.zwz.mod_upload.util.CheckUtil;
import org.zwz.mod_upload.util.MusicUtil;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @PostMapping("/upImage")
    public Mess upLoadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Mess.fail().code(400).message("文件不能为空");
        }
        
        if (CheckUtil.isImage(file.getOriginalFilename())) {
            String url = uploadService.uploadImage(file);
            if (url != null) {
                return Mess.success().message("文件上传成功").data("url", url);
            } else {
                return Mess.fail().code(500).message("文件上传失败");
            }
        }
        return Mess.fail().code(400).message("文件格式错误，请上传图片文件");
    }

    @PostMapping("/upLyric")
    public Mess upLoadLyric(MultipartFile file) {
        if (CheckUtil.isLyric(file.getOriginalFilename())) {
            String url = uploadService.uploadLyric(file);
            return Mess.success().message("文件上传成功").data("url", url);
        }
        return Mess.fail().message("文件格式错误，请上传歌词文件");
    }

    @PostMapping("/upMusic")
    public Mess upLoadMusic(MultipartFile file) {
        if (CheckUtil.isMusic(file.getOriginalFilename())) {
            Integer length = MusicUtil.getDuration(file);
            String url = uploadService.uploadMusic(file);
            return Mess.success().message("音乐上传成功").data("url", url).data("timelength", length);
        }
        return Mess.fail().message("文件格式错误，请上传音频文件");
    }
    
}