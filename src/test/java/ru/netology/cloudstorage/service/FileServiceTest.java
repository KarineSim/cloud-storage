package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.entity.AuthorizedUsers;
import ru.netology.cloudstorage.entity.UploadedFiles;
import ru.netology.cloudstorage.enums.Status;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.model.NewFileName;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.security.JwtTokenProvider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest
class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserRepository userRepository;

    private final String LOGIN = "sema";
    private final String PASSWORD = "pass2";
    private final String AUTH_TOKEN = UUID.randomUUID().toString();
    private final String FILE_NAME = "test.txt";
    private final NewFileName NEW_FILE_NAME = new NewFileName("new.txt");
    private AuthorizedUsers authUser;

    private UploadedFiles file;

    @BeforeEach
    void setUp() {
        authUser = AuthorizedUsers.builder()
                .id(2L)
                .login(LOGIN)
                .password(PASSWORD)
                .build();
        file = UploadedFiles.builder()
                .fileName(FILE_NAME)
                .size(7L)
                .fileContent("content".getBytes())
                .status(Status.ACTIVE)
                .type(MediaType.TEXT_PLAIN_VALUE)
                .userId(AuthorizedUsers.builder().id(2L).build())
                .build();
        when(jwtTokenProvider.getLoginFromToken(AUTH_TOKEN.substring(7))).thenReturn(authUser.getLogin());
        when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.ofNullable(authUser));
        when(fileRepository.findByUserIdAndFileName(authUser, FILE_NAME)).thenReturn(file);
    }

    @Test
    void uploadFileTest() {
        when(fileRepository.findByFileName(FILE_NAME)).thenReturn(null);

        MultipartFile multipartFile = new MockMultipartFile("test", FILE_NAME, MediaType.TEXT_PLAIN_VALUE, file.getFileContent());

        ResponseEntity<String> actual = fileService.uploadFile(AUTH_TOKEN, FILE_NAME, multipartFile);
        ResponseEntity<String> expected = ResponseEntity.ok().body("Файл " + FILE_NAME + " сохранен");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void deleteFileTest() {
        ResponseEntity<String> actual = fileService.deleteFile(AUTH_TOKEN, FILE_NAME);
        ResponseEntity<String> expected = ResponseEntity.ok().body("Файл " + FILE_NAME + " удален");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void downloadFileTest() {
        ResponseEntity<byte[]> actual = fileService.downloadFile(AUTH_TOKEN, FILE_NAME);
        ResponseEntity<byte[]> expected = ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getFileContent());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void editFileNameTest() {
        ResponseEntity<String> actual = fileService.editFileName(AUTH_TOKEN, FILE_NAME, NEW_FILE_NAME);
        ResponseEntity<String> expected = ResponseEntity.ok().body("Имя файла " + FILE_NAME + " изменено на " + NEW_FILE_NAME.getFileName());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAllFilesTest() {
        int limit = 2;

        List<UploadedFiles> list = List.of(
                UploadedFiles.builder().id(1L).fileName("1.txt").size(3L).build(),
                UploadedFiles.builder().id(2L).fileName("2.txt").size(5L).build(),
                UploadedFiles.builder().id(3L).fileName("3.txt").size(7L).build()
        );

        List<File> returnList = List.of(
                new File("1.txt", Math.toIntExact(3L)),
                new File("2.txt", Math.toIntExact(5L))
        );

        when(fileRepository.findAllByUserId(authUser)).thenReturn(Optional.of(list));

        ResponseEntity<List<File>> actual = fileService.getAllFiles(AUTH_TOKEN, limit);
        ResponseEntity<List<File>> expected = ResponseEntity.ok().body(returnList);

        Assertions.assertEquals(expected, actual);
    }
}