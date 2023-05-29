package ru.netology.cloudstorage.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.netology.cloudstorage.entity.AuthorizedUsers;
import ru.netology.cloudstorage.model.AuthRequest;
import ru.netology.cloudstorage.model.AuthResponse;
import ru.netology.cloudstorage.repository.UserRepository;
import ru.netology.cloudstorage.security.JwtTokenProvider;

@Slf4j
@AllArgsConstructor
@Service
public class AuthorizationService {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
        String token;
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            token = jwtTokenProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        log.info("Пользователь " + authRequest.getLogin() + " авторизовался");
        return ResponseEntity.ok().body(new AuthResponse(token));
    }

    public ResponseEntity<Void> logout(String authToken, HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        AuthorizedUsers authUsers = userRepository.findByLogin(auth.getPrincipal().toString())
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));

        SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
        securityContextLogoutHandler.logout(request, response, auth);

        jwtTokenProvider.removeToken(authToken);

        log.info("Пользователь " + authUsers.getLogin() + " вышел из системы");
        return ResponseEntity.ok().body(null);
    }
}
