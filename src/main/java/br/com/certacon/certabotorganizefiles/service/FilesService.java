package br.com.certacon.certabotorganizefiles.service;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class FilesService {

    private final FilesRepository filesRepository;

    public FilesService(FilesRepository filesRepository) {

        this.filesRepository = filesRepository;
    }

    public FilesEntity saveOrUpdate(FilesEntity entity) {

        if (validate(entity).equals(Boolean.TRUE)) {
            entity.setCreatedAt(new Date());
            entity.setStatus(FileStatus.CREATED);
            return filesRepository.save(entity);

        } else {
            throw new IllegalArgumentException("Nome do arquivo não pode ser nulo ou vazio");
        }
    }

    private FilesEntity update(FilesEntity existingEntity, FilesEntity updatedEntity) {
        // Atualizar somente os campos necessários
        existingEntity.setFileName(updatedEntity.getFileName());
        existingEntity.setCreatedAt(updatedEntity.getCreatedAt());
        existingEntity.setUpdatedAt(new Date());
        existingEntity.setStatus(FileStatus.UPDATED);
        return existingEntity;
    }

    private Boolean validate(FilesEntity entity) {

        if (entity.getFileName() == null || entity.getFileName().isEmpty()) {
            return Boolean.FALSE;
        } else {
            return Boolean.TRUE;
        }
    }

    public Boolean delete(UUID id) throws FileNotFoundException {

        Optional<FilesEntity> modelOptional = filesRepository.findById(id);

        if (modelOptional.isPresent()) {
            filesRepository.delete(modelOptional.get());
            return Boolean.TRUE;
        } else {
            throw new FileNotFoundException("Arquivo não existe");
        }
    }

}
