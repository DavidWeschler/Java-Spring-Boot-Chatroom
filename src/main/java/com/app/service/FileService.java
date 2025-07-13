package com.app.service;

import com.app.model.File;
import com.app.repo.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public File saveFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            return null;
        }
        String mimeType = multipartFile.getContentType();
        File file = new File();
        file.setFilename(multipartFile.getOriginalFilename());
        file.setFileData(multipartFile.getBytes());
        file.setUploadedAt(LocalDateTime.now());
        file.setMimeType(mimeType);
        return fileRepository.save(file);
    }

    public File getFileById(Long id) {
        return fileRepository.findById(id).orElse(null);
    }
}
