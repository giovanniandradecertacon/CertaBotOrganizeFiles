package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@Service
@Slf4j
public class MoveFilesComponent {
    private final MoveFilesHelper helper;
    private final FilesRepository filesRepository;

    public MoveFilesComponent(MoveFilesHelper helper, FilesRepository filesRepository) {
        this.helper = helper;
        this.filesRepository = filesRepository;
    }

    public FilesEntity moveFiles(File loadDirectory) throws FileNotFoundException {
        if (loadDirectory.exists()) {
            FilesEntity saveFile = null;
            PathCreationEntity pathComponents = helper.pathSplitter(loadDirectory);
            pathComponents.setPath(loadDirectory.getPath());
            File[] listFiles = loadDirectory.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                saveFile = FilesEntity.builder()
                        .filePath(listFiles[i].getPath())
                        .fileName(listFiles[i].getName())
                        .status(FileStatus.CREATED)
                        .build();
                filesRepository.save(saveFile);
                if (FileNameUtils.getExtension(listFiles[i].getName()).equals("txt")
                        || FileNameUtils.getExtension(listFiles[i].getName()).equals("zip")
                        || FileNameUtils.getExtension(listFiles[i].getName()).equals("rar")
                        || FileNameUtils.getExtension(listFiles[i].getName()).equals("xml")) {
                    Path organizedEFDPath = helper.pathCreatorForToOrganize(pathComponents);
                    FileStatus fileStatus = helper.moveFile(listFiles[i], Path.of(organizedEFDPath + File.separator + listFiles[i].getName()), ATOMIC_MOVE);
                    saveFile.setStatus(fileStatus);
                }
            }
            return saveFile;
        } else {
            throw new FileNotFoundException("Diretório não existente");
        }
    }
}
