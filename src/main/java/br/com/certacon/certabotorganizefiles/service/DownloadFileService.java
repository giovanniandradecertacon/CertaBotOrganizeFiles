package br.com.certacon.certabotorganizefiles.service;

import br.com.certacon.certabotorganizefiles.entity.UserFilesEntity;
import br.com.certacon.certabotorganizefiles.repository.UserFilesRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class DownloadFileService {
    private final UserFilesRepository userFilesRepository;

    public DownloadFileService(UserFilesRepository userFilesRepository) {
        this.userFilesRepository = userFilesRepository;
    }

    public UserFilesEntity getById(UUID id) {
        Optional<UserFilesEntity> model = userFilesRepository.findById(id);
        if (!model.isPresent()) {
            throw new RuntimeException("Objeto não foi encontrado!");
        }
        UserFilesEntity result = model.get();
        return result;
    }
}
