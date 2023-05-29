package ru.netology.cloudstorage.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.AuthorizedUsers;
import ru.netology.cloudstorage.entity.UploadedFiles;
import ru.netology.cloudstorage.enums.Status;
import ru.netology.cloudstorage.exception.DuplicateFileNameException;
import ru.netology.cloudstorage.exception.FileNotFoundException;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.model.NewFileName;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.security.JwtTokenProvider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class FileService {

    private final FileRepository fileRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ResponseEntity<String> uploadFile(String authToken, String fileName, MultipartFile multipartFile) {
        AuthorizedUsers user = findUserInRepository(authToken);

        UploadedFiles uploadedFile = fileRepository.findByFileName(fileName);
        if (uploadedFile != null) {
            throw new DuplicateFileNameException("Файл с именем " + fileName + " уже существует");
        }

        UploadedFiles file = null;
        try {
            file = UploadedFiles.builder()
                    .fileName(fileName)
                    .status(Status.ACTIVE)
                    .size(multipartFile.getSize())
                    .type(multipartFile.getContentType())
                    .fileContent(multipartFile.getBytes())
                    .userId(AuthorizedUsers.builder().id(user.getId()).build())
                    .build();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        fileRepository.save(file);

        log.info("Пользователь с id {} успешно загрузил файл {}", user.getId(), fileName);
        return ResponseEntity.ok().body("Файл " + fileName + " сохранен");
    }

    @Transactional
    public ResponseEntity<String> deleteFile(String authToken, String fileName) {
        AuthorizedUsers user = findUserInRepository(authToken);

        UploadedFiles file = fileRepository.findByUserIdAndFileName(user, fileName);
        if (file == null) {
            throw new FileNotFoundException("У пользователя " + user.getLogin() + " не найден файл с именем " + fileName);
        }

        fileRepository.deleteByUserIdAndFileName(user, fileName);

        log.info("Пользователь с id {} успешно удалил файл {}", user.getId(), fileName);
        return ResponseEntity.ok().body("Файл " + fileName + " удален");
    }


    @Transactional
    public ResponseEntity<byte[]> downloadFile(String authToken, String fileName) {
        AuthorizedUsers user = findUserInRepository(authToken);

        UploadedFiles file = fileRepository.findByUserIdAndFileName(user, fileName);
        if (file == null) {
            throw new FileNotFoundException("У пользователя " + user.getLogin() + " не найден файл с именем " + fileName);
        }

        log.info("Пользователь с id {} успешно скачал файл {}", user.getId(), fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getFileContent());
    }

    @Transactional
    public ResponseEntity<String> editFileName(String authToken, String fileName, NewFileName newFileName) {
        AuthorizedUsers user = findUserInRepository(authToken);

        UploadedFiles fileDB = fileRepository.findByUserIdAndFileName(user, fileName);
        if (fileDB == null) {
            throw new FileNotFoundException("У пользователя " + user.getLogin() + " не найден файл с именем " + fileName);
        }

        UploadedFiles newFileNameDB = fileRepository.findByFileName(newFileName.getFileName());
        if (newFileNameDB != null) {
            throw new DuplicateFileNameException("Файл с именем " + newFileName.getFileName() + " уже существует");
        }

        fileDB.setFileName(newFileName.getFileName());
        fileRepository.save(fileDB);

        log.info("Пользователь с id {} успешно изменил имя файла {} на {}", user.getId(), fileName, newFileName.getFileName());
        return ResponseEntity.ok().body("Имя файла " + fileName + " изменено на " + newFileName.getFileName());
    }

    @Transactional
    public ResponseEntity<List<File>> getAllFiles(String authToken, int limit) {
        AuthorizedUsers user = findUserInRepository(authToken);

        Optional<List<UploadedFiles>> files = fileRepository.findAllByUserId(user);

        List<File> filesList = files.get().stream()
                .map(f -> new File(f.getFileName(), Math.toIntExact(f.getSize())))
                .limit(limit)
                .collect(Collectors.toList());

        log.info("Пользователь с id {} получил список файлов", user.getId());
        return ResponseEntity.ok().body(filesList);
    }

    private AuthorizedUsers findUserInRepository(String authToken) {
        String login = jwtTokenProvider.getLoginFromToken(authToken.substring(7));

        Optional<AuthorizedUsers> user = userRepository.findByLogin(login);

        return user.get();
    }
}
