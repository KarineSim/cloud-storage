package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import ru.netology.cloudstorage.entity.AuthorizedUsers;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private final String LOGIN = "sema";
    private final String PASSWORD = "pass2";
    private final AuthorizedUsers AUTH_USER = AuthorizedUsers.builder()
            .id(2L)
            .login(LOGIN)
            .password(PASSWORD)
            .build();

    @Test
    void loadUserByUsernameTest() {
        when(userRepository.findByLogin(LOGIN)).thenReturn(Optional.ofNullable(AUTH_USER));

        UserDetails actual = userDetailsService.loadUserByUsername(LOGIN);
        UserDetails expected = new User(LOGIN, PASSWORD, true, true, true, true, new HashSet<>());

        Assertions.assertEquals(expected, actual);
    }
}