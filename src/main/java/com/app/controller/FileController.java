package com.app.controller;

import com.app.model.File;
import com.app.model.User;
import com.app.service.ChatroomService;
import com.app.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private ChatroomService chatroomService;

    @PostMapping("/{chatroomId}/upload")
    @ResponseBody
    public ResponseEntity<?> uploadFile(@PathVariable Long chatroomId, @RequestParam("file") MultipartFile multipartFile) {

        System.out.println("DEBUG: Upload requested for chatroomId = " + chatroomId);

        // if you are admin, throw exception

        // Ensure user is member of chatroom
        User user = chatroomService.requireMembershipOrThrow(chatroomId);

        try {
            File file = fileService.saveFile(multipartFile);
            return ResponseEntity.ok().body(Map.of(
                    "fileId", file.getId(),
                    "filename", file.getFilename()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        System.out.println("DEBUG: Download requested for file id = " + id);

        // if you are admin, check if the ID of the msg is reported and if not, throw exception

        File file = fileService.getFileById(id);

        if (file == null) {
            System.out.println("DEBUG: No file found for id = " + id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                    .body("File wasn't available on site (not found)".getBytes(StandardCharsets.UTF_8));
        }

        System.out.println("DEBUG: File found: " + file.getFilename());

        if (file.getFileData() == null) {
            System.out.println("DEBUG: fileData is NULL for id = " + id);
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                    .body("File wasn't available on site (no data)".getBytes(StandardCharsets.UTF_8));
        }

        System.out.println("DEBUG: File size = " + file.getFileData().length + " bytes");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(file.getFilename(), StandardCharsets.UTF_8)
                .build());

        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }
}
