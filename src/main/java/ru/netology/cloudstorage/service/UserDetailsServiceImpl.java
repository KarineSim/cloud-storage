package ru.netology.cloudstorage.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.cloudstorage.entity.AuthorizedUsers;
import ru.netology.cloudstorage.repository.UserRepository;

import java.util.HashSet;

@Slf4j
@AllArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        AuthorizedUsers authUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " not found"));

        return new User(authUser.getLogin(), authUser.getPassword(), true, true, true, true, new HashSet<>());
    }
}
