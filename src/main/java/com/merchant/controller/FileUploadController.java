package com.merchant.controller;

import com.merchant.dto.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("请选择文件");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return Result.error("只能上传图片文件");
        if (file.getSize() > 5 * 1024 * 1024) return Result.error("图片大小不能超过5MB");
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
            String projectRoot = System.getProperty("user.dir");
            File uploadDir = new File(projectRoot, uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            file.transferTo(new File(uploadDir, newFilename));
            return Result.success("上传成功", "/uploads/" + newFilename);
        } catch (IOException e) {
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}
