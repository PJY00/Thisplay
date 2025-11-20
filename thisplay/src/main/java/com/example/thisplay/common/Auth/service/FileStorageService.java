package com.example.thisplay.common.Auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final Path uploadDir = Paths.get("uploads/profile");

    public String saveProfileImage(Long userId, MultipartFile file) throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String filename = "user-" + userId + "-" + System.currentTimeMillis() + "." + ext;

        Path target = uploadDir.resolve(filename);
        file.transferTo(target.toFile());

        // 브라우저에서 접근할 URL (아래 ResourceHandler와 맞춰야 함)
        return "/uploads/profile/" + filename;
    }
}