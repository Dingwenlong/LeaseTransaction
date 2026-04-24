package com.campus.lease.controller;

import com.campus.lease.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Tag(name = "文件服务", description = "图片文件上传接口")
@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${file.upload.path:./uploads}")
    private String uploadPath;

    @Value("${file.access.path:/uploads}")
    private String accessPath;

    @Value("${file.public-base-url:http://localhost:8081}")
    private String publicBaseUrl;

    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Operation(summary = "上传图片文件", description = "上传图片到本地文件目录并返回访问 URL，仅支持 jpg、jpeg、png、gif、webp，大小不超过 10MB")
    @PostMapping("/upload")
    public Result<Map<String, String>> uploadFile(
            @Parameter(description = "要上传的图片文件")
            @RequestParam("file") MultipartFile file
    ) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("文件大小不能超过10MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (!isAllowedExtension(extension)) {
            return Result.error("不支持的文件格式，仅支持jpg、jpeg、png、gif、webp");
        }

        try {
            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String saveDir = uploadPath + File.separator + datePath;
            Path savePath = Paths.get(saveDir);

            if (!Files.exists(savePath)) {
                Files.createDirectories(savePath);
            }

            String newFilename = UUID.randomUUID().toString() + "." + extension;
            String fullPath = saveDir + File.separator + newFilename;
            file.transferTo(new File(fullPath));

            String fileUrl = publicBaseUrl.replaceAll("/$", "") + accessPath + "/" + datePath + "/" + newFilename;
            String relativePath = datePath + "/" + newFilename;

            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("path", relativePath);
            result.put("filename", newFilename);

            return Result.success(result);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
