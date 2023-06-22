package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.UserFilesEntity;
import br.com.certacon.certabotorganizefiles.repository.UserFilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@Component
@Slf4j
public class SaveFilesForRestComponent {
    private final UserFilesRepository userFilesRepository;

    public SaveFilesForRestComponent(UserFilesRepository userFilesRepository) {
        this.userFilesRepository = userFilesRepository;
    }

    public UserFilesEntity saveFilesForRestEFDPadrao(FilesEntity entity) throws IOException {

        String mimeType = Files.probeContentType(Path.of(entity.getFilePath()));
        UserFilesEntity userFilesEntity = UserFilesEntity.builder()
                .companyName(entity.getCompanyName())
                .fileName(entity.getFileName())
                .createdAt(new Date())
                .path(entity.getFilePath())
                .cnpj(entity.getCnpj())
                .status(FileStatus.CREATED_EFD)
                .mimeType(mimeType)
                .ipServer(entity.getIpServer())
                .build();

        return userFilesRepository.save(userFilesEntity);
    }

    public UserFilesEntity saveFilesForRestNFe(FilesEntity entity) throws IOException {

        String mimeType = Files.probeContentType(Path.of(entity.getFilePath()));
        UserFilesEntity userFilesEntity = UserFilesEntity.builder()
                .companyName(entity.getCompanyName())
                .fileName(entity.getFileName())
                .createdAt(new Date())
                .path(entity.getFilePath())
                .cnpj(entity.getCnpj())
                .status(FileStatus.CREATED_NFE)
                .mimeType(mimeType)
                .ipServer(entity.getIpServer())
                .build();

        return userFilesRepository.save(userFilesEntity);
    }

    public UserFilesEntity saveFilesForRestCFe(FilesEntity entity) throws IOException {
        String mimeType = Files.probeContentType(Path.of(entity.getFilePath()));
        UserFilesEntity userFilesEntity = UserFilesEntity.builder()
                .companyName(entity.getCompanyName())
                .fileName(entity.getFileName())
                .createdAt(new Date())
                .path(entity.getFilePath())
                .cnpj(entity.getCnpj())
                .status(FileStatus.CREATED_CFE)
                .mimeType(mimeType)
                .ipServer(entity.getIpServer())
                .build();

        return userFilesRepository.save(userFilesEntity);
    }
}
