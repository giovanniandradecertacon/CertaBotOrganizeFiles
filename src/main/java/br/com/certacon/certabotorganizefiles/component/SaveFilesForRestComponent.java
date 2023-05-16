package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.entity.UserFilesEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
import br.com.certacon.certabotorganizefiles.repository.UserFilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.Date;

@Component
@Slf4j
public class SaveFilesForRestComponent {
    private final UserFilesRepository userFilesRepository;
    private final MoveFilesHelper helper;

    public SaveFilesForRestComponent(UserFilesRepository userFilesRepository, MoveFilesHelper helper) {
        this.userFilesRepository = userFilesRepository;
        this.helper = helper;
    }

    public UserFilesEntity saveFilesForRest(FilesEntity entity) throws FileNotFoundException {
        PathCreationEntity informationsForSave = helper.pathSplitter(new File(entity.getFilePath()));
        Path folder = null;
        if (entity.getFileName().startsWith("XMLS-")) {
            folder = helper.pathCreatorWithObrigacaoAcessoria(informationsForSave, FileFoldersFunction.ORGANIZADOS, FileType.EFDPadrao);
        }
        if (entity.getFileName().startsWith("EFDS-")) {
            folder = helper.pathCreatorWithObrigacaoAcessoria(informationsForSave, FileFoldersFunction.ORGANIZADOS, FileType.EFDPadrao);
        }

        UserFilesEntity userFilesEntity = UserFilesEntity.builder()
                .fileName(entity.getFileName())
                .createdAt(new Date())
                .path(folder.toString())
                .cnpj(informationsForSave.getCnpj())
                .ipServer(informationsForSave.getIpServer())
                .build();
        userFilesRepository.save(userFilesEntity);
        log.info(informationsForSave.getIpServer());
        log.info(entity.getFileName());
        log.info(informationsForSave.getPath());
        log.info(informationsForSave.getCnpj());
        log.info(informationsForSave.getRoot());

        return userFilesEntity;
    }
}
