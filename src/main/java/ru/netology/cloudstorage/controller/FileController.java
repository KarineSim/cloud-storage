package ru.netology.cloudstorage.controller;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.model.NewFileName;
import ru.netology.cloudstorage.service.FileService;

import java.util.List;

@AllArgsConstructor
@RestController
public class FileController {

    private final FileService fileService;

    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(@RequestHeader("auth-token") @NotNull String authToken, @RequestParam("filename") @NotNull String fileName, @RequestBody @NotNull MultipartFile file) {
        return fileService.uploadFile(authToken, fileName, file);
    }

    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(@RequestHeader("auth-token") @NotNull String authToken, @RequestParam("filename") @NotNull String fileName) {
        return fileService.deleteFile(authToken, fileName);
    }

    @GetMapping("/file")
    public ResponseEntity<byte[]> downloadFile(@RequestHeader("auth-token") @NotNull String authToken, @RequestParam("filename") @NotNull String fileName) {
        return fileService.downloadFile(authToken, fileName);
    }

    @PutMapping("/file")
    public ResponseEntity<String> editFileName(@RequestHeader("auth-token") @NotNull String authToken, @RequestParam("filename") @NotNull String fileName, @RequestBody @Validated NewFileName newFileName) {
        return fileService.editFileName(authToken, fileName, newFileName);
    }

    @GetMapping("/list")
    public ResponseEntity<List<File>> getAllFiles(@RequestHeader("auth-token") @NotNull String authToken, @RequestParam("limit") @NotNull @Min(1) int limit) {
        return fileService.getAllFiles(authToken, limit);
    }
}
