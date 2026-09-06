package in.rohitahire.smartmartpos.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService {

    private static final String UPLOAD_DIR = "uploads";

    public String saveFile(MultipartFile file) throws IOException {

        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath();
        Files.createDirectories(uploadPath);

        String original = file.getOriginalFilename();
        String extension = "";

        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID() + extension;
        Path target = uploadPath.resolve(fileName);

        file.transferTo(target.toFile());

        return "http://localhost:8080/uploads/" + fileName;
    }
}