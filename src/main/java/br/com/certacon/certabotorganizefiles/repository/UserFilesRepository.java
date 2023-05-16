package br.com.certacon.certabotorganizefiles.repository;

import br.com.certacon.certabotorganizefiles.entity.UserFilesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserFilesRepository extends JpaRepository<UserFilesEntity, UUID> {
}
