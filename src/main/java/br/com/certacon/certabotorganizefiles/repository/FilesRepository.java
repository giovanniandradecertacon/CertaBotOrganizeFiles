package br.com.certacon.certabotorganizefiles.repository;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FilesRepository extends JpaRepository<FilesEntity, UUID> {
    Optional<FilesEntity> findByFileName(String fileName);

    List<FilesEntity> findAllByStatusEquals(FileStatus fileStatus);
}
