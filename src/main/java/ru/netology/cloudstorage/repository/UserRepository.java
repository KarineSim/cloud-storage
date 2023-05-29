package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entity.AuthorizedUsers;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AuthorizedUsers, Long> {

    Optional<AuthorizedUsers> findByLogin(String login);

}
