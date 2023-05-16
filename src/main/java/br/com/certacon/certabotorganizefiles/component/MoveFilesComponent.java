package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@Component
@Slf4j
public class MoveFilesComponent {
    private final MoveFilesHelper helper;
    private final FilesRepository filesRepository;

    public MoveFilesComponent(MoveFilesHelper helper, FilesRepository filesRepository) {
        this.helper = helper;
        this.filesRepository = filesRepository;
    }

    public FilesEntity moveFiles(FilesEntity entity) throws FileNotFoundException {
        File entityFile = new File(entity.getFilePath());
        if (entityFile.exists() && entityFile.isDirectory()) {
            PathCreationEntity pathComponents = helper.pathSplitter(entityFile);
            pathComponents.setPath(entityFile.getPath());
            File[] listFiles;
            do {
                listFiles = entityFile.listFiles();
                for (int i = 0; i < listFiles.length; i++) {
                    if (FileNameUtils.getExtension(listFiles[i].getName()).equals("txt")
                            || FileNameUtils.getExtension(listFiles[i].getName()).equals("zip")
                            || FileNameUtils.getExtension(listFiles[i].getName()).equals("rar")
                            || FileNameUtils.getExtension(listFiles[i].getName()).equals("xml")
                            || listFiles[i].isDirectory()) {
                        Path organizeEFDPath = helper.pathCreatorForToOrganize(pathComponents);
                        FileStatus fileStatus = helper.moveFile(listFiles[i], Path.of(organizeEFDPath + File.separator + listFiles[i].getName()), ATOMIC_MOVE);
                        entity.setStatus(fileStatus);
                        entity.setFilePath(organizeEFDPath.toString() + File.separator + listFiles[i].getName());
                        entity.setFileName(listFiles[i].getName());
                    }
                }
            } while (listFiles.length == 0);

            return entity;
        } else {
            throw new FileNotFoundException("Diretório não existente");
        }
    }
}
