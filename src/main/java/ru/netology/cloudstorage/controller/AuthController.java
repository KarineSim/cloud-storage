package ru.netology.cloudstorage.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.cloudstorage.model.AuthRequest;
import ru.netology.cloudstorage.model.AuthResponse;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.service.AuthorizationService;


@AllArgsConstructor
@RestController
public class AuthController {

    UserRepository userRepository;
    private final AuthorizationService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Validated AuthRequest authRequest) {
        return authService.login(authRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("auth-token") @NotNull String authToken, HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(authToken, request, response);
    }

}
