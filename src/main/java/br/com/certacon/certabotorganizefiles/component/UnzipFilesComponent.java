package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.UnzipAndZipFilesHelper;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@Component
@Slf4j
public class UnzipFilesComponent {
    private final UnzipAndZipFilesHelper helper;

    public UnzipFilesComponent(UnzipAndZipFilesHelper helper) {
        this.helper = helper;
    }

    public FilesEntity MoveAndUnzip(FilesEntity entity) throws IOException {
        File zipFile = new File(entity.getFilePath());
        if (zipFile.exists()) {
            PathCreationEntity pathComponents = helper.pathSplitter(zipFile);
            List<Path> paths = helper.directoryCreator(pathComponents);

            File uuidDir = paths.get(0).getParent().toFile();
            File compactedDir = paths.get(0).toFile();
            File descompactedDir = paths.get(1).toFile();

            Path destPath = Files.move(zipFile.toPath(), Path.of(compactedDir.toPath() + File.separator + zipFile.getName()), ATOMIC_MOVE);

            FileStatus status = helper.unzipFile(destPath.toFile(), descompactedDir);
            Files.deleteIfExists(destPath);

            if (status.equals(FileStatus.UNZIPPED)) {
                File[] descompactedList;
                do {
                    descompactedList = descompactedDir.listFiles();
                    if (descompactedList.length > 0) {
                        for (int i = 0; i < descompactedList.length; i++) {
                            if (descompactedList[i].isDirectory()) {
                                FileStatus fileStatus = helper.extractFolder(descompactedList[i], compactedDir);
                                log.info(fileStatus.name());
                            }
                            if (FileNameUtils.getExtension(descompactedList[i].getName()).equals("zip")
                                    || FileNameUtils.getExtension(descompactedList[i].getName()).equals("rar")) {
                                Files.move(descompactedList[i].toPath(), Path.of(compactedDir + descompactedList[i].getName()));

                            }
                        }
                    }

                    File[] compactedList = compactedDir.listFiles();
                    if (compactedList.length > 0) {
                        for (int i = 0; i < compactedList.length; i++) {
                            FileStatus fileStatus = helper.unzipFile(compactedList[i], descompactedDir);
                            Files.deleteIfExists(compactedList[i].toPath());
                            log.info(fileStatus.name());
                        }
                    }
                    descompactedList = descompactedDir.listFiles();
                } while (helper.checkFolderExistence(descompactedList) == Boolean.TRUE);

                Files.deleteIfExists(compactedDir.toPath());

                helper.extractFolder(descompactedDir, descompactedDir.getParentFile());

            }

            entity.setFileName(uuidDir.getName());
            entity.setStatus(FileStatus.EXTRACTED);
            entity.setFilePath(uuidDir.getPath());
            return entity;
        } else {
            throw new FileNotFoundException("Arquivo não foi encontrado");
        }

    }
}
