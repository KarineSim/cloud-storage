package ru.netology.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.netology.cloudstorage.entity.AuthorizedUsers;
import ru.netology.cloudstorage.entity.UploadedFiles;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<UploadedFiles, Long> {

    void deleteByUserIdAndFileName(AuthorizedUsers user, String fileName);

    UploadedFiles findByUserIdAndFileName(AuthorizedUsers user, String fileName);

    Optional<List<UploadedFiles>> findAllByUserId(AuthorizedUsers user);

    UploadedFiles findByFileName(String fileName);
}
