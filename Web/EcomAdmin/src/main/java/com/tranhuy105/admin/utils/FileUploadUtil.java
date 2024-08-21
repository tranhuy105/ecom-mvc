package com.tranhuy105.admin.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
public class FileUploadUtil {
    public static String validateAndGetImageFilename(MultipartFile avatar) {
        String originalFilename = avatar.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        if (originalFilename.length() > 60) {
            throw new IllegalArgumentException("Invalid filename, accept maximum 60 characters");
        }
        int lastDotIndex = originalFilename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
            throw new IllegalArgumentException("File extension cannot be determined");
        }

        String fileExtension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        if (!fileExtension.matches("jpg|jpeg|png")) {
            throw new IllegalArgumentException("Invalid file type. Only jpg, jpeg, and png are allowed.");
        }
        return UUID.randomUUID() + "." + fileExtension;
    }

    public static void saveFile(String uploadDir, String fileName, MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioe) {
            throw new IOException("Could not save the file: ", ioe);
        }
    }

    public static void cleanDir(String dir) {
        Path dirPath = Paths.get(dir);

        try (Stream<Path> files = Files.list(dirPath)) {
            files.forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException ex) {
                        log.warn("Couldn't delete file: " + file.toAbsolutePath());
                    }
                }
            });
        } catch (IOException e) {
            log.warn("Couldn't list the directory: " + e.getMessage());
        }
    }
}
