package com.snbnk.documentation.storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalFileStorageService implements FileStorageService{

    private final Path root = Paths.get("uploads");

    @Override
    public String store(MultipartFile file) {
        try {

            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = root.resolve(filename);

            Files.copy(file.getInputStream(), path);

            return path.toString();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    @Override
    public Resource load(String path) {
        try {
            return new UrlResource(Paths.get(path).toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
