package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@Component
public class OrganizeComponent {
    private final FilesRepository filesRepository;
    private final MoveFilesHelper helper;

    public OrganizeComponent(FilesRepository filesRepository, MoveFilesHelper helper) {
        this.filesRepository = filesRepository;
        this.helper = helper;
    }

    public FileStatus organizeFilesForZipping(File uuidDirectory, UUID uuid) throws IOException {
        File[] filesList = uuidDirectory.listFiles();
        Optional<FilesEntity> fileEntity = filesRepository.findById(uuid);
        if (fileEntity.isPresent()) {
            for (int i = 0; i < filesList.length; i++) {

                if (FileNameUtils.getExtension(filesList[i].getName()).equals("xml")) {
                    Path xmlFolder = Path.of(filesList[i].getParentFile() + File.separator + "XMLS-" + fileEntity.get().getId().toString().toUpperCase());
                    if (!xmlFolder.toFile().exists()) xmlFolder.toFile().mkdirs();
                    Files.move(filesList[i].toPath(), Path.of(xmlFolder + File.separator + filesList[i].getName()), ATOMIC_MOVE);

                } else if (FileNameUtils.getExtension(filesList[i].getName()).equals("txt")) {
                    Path efdFolder = Path.of(filesList[i].getParentFile() + File.separator + "EFDS-" + fileEntity.get().getId().toString().toUpperCase());
                    if (!efdFolder.toFile().exists()) efdFolder.toFile().mkdirs();
                    Files.move(filesList[i].toPath(), Path.of(efdFolder + File.separator + filesList[i].getName()), ATOMIC_MOVE);
                } else if (FileNameUtils.getExtension(filesList[i].getName()).equals("zip")) {
                    String parentFile = new File(filesList[i].getPath()).getParentFile().getName();
                    String loadPath = filesList[i].getPath().replace("ORGANIZAR", "CARREGAMENTO");
                    String finalPath = loadPath.replace(File.separator + parentFile, "");
                    Files.move(filesList[i].toPath(), Path.of(finalPath), ATOMIC_MOVE);
                } else {
                    PathCreationEntity creationEntity = helper.pathSplitter(filesList[i]);
                    Path archivedFolder = Path.of(creationEntity.getRoot()
                            + File.separator + FileFoldersFunction.ARQUIVADOS
                            + File.separator + creationEntity.getIpServer()
                            + File.separator + creationEntity.getCnpj()
                            + File.separator + creationEntity.getYear());
                    Files.move(filesList[i].toPath(), Path.of(archivedFolder + File.separator + filesList[i].getName()), ATOMIC_MOVE);
                }
            }
            return FileStatus.READY;
        } else {
            throw new RuntimeException("Objeto não está presente");
        }
    }
}

