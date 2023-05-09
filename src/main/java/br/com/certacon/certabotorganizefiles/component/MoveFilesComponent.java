package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
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

    public MoveFilesComponent(MoveFilesHelper helper) {
        this.helper = helper;
    }

    public FilesEntity moveFiles(File loadDirectory) throws FileNotFoundException {
        if (loadDirectory.exists()) {
            PathCreationEntity pathComponents = helper.pathSplitter(loadDirectory);
            pathComponents.setPath(loadDirectory.getPath());
            File[] listFiles = loadDirectory.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (FileNameUtils.getExtension(listFiles[i].getName()).equals("txt")
                        || FileNameUtils.getExtension(listFiles[i].getName()).equals("zip")
                        || FileNameUtils.getExtension(listFiles[i].getName()).equals("xml")) {
                    Path organizedEFDPath = helper.pathCreatorForToOrganize(pathComponents);
                    FileStatus fileStatus = helper.moveFile(listFiles[i], Path.of(organizedEFDPath + File.separator + listFiles[i].getName()), ATOMIC_MOVE);
                    log.info(String.valueOf(fileStatus));
                }
            }
            return FilesEntity.builder().build();
        } else {
            throw new FileNotFoundException("Diretório não existente");
        }
    }
}
