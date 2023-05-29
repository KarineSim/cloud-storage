package ru.netology.cloudstorage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.cloudstorage.entity.AuthorizedUsers;
import ru.netology.cloudstorage.model.AuthRequest;
import ru.netology.cloudstorage.model.AuthResponse;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.security.JwtTokenProvider;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

@SpringBootTest
class AuthorizationServiceTest {

    @InjectMocks
    private AuthorizationService authService;

    @Mock
    private AuthenticationManager authManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private UserRepository userRepository;

    @Mock
    HttpServletRequest request;
    @Mock
    HttpServletResponse response;

    private final String LOGIN = "sema";
    private final String PASSWORD = "pass2";
    private final String AUTH_TOKEN = UUID.randomUUID().toString();
    private final AuthRequest AUTH_REQUEST = new AuthRequest(LOGIN, PASSWORD);
    private final AuthResponse AUTH_RESPONSE = new AuthResponse(AUTH_TOKEN);
    private final AuthorizedUsers AUTH_USER = AuthorizedUsers.builder()
            .id(1L)
            .login(LOGIN)
            .password(PASSWORD)
            .build();

    @Test
    void loginTest() {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(LOGIN, PASSWORD));
        when(jwtTokenProvider.generateToken(auth)).thenReturn(AUTH_TOKEN);

        ResponseEntity<AuthResponse> actual = authService.login(AUTH_REQUEST);
        ResponseEntity<AuthResponse> expected = ResponseEntity.ok().body(AUTH_RESPONSE);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void logoutTest() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(AUTH_REQUEST, null, null));
        Authentication auth2 = SecurityContextHolder.getContext().getAuthentication();
        when(userRepository.findByLogin(auth2.getPrincipal().toString())).thenReturn(Optional.ofNullable(AUTH_USER));

        ResponseEntity<Void> actual = authService.logout(AUTH_TOKEN, request, response);
        ResponseEntity<Void> expected = ResponseEntity.ok().body(null);

        Assertions.assertEquals(expected, actual);
    }
}