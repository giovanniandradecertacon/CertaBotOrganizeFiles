package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.UnzipAndZipFilesHelper;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
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

    public FilesEntity MoveAndUnzip(File zipFile) throws IOException {
        if (zipFile.exists()) {
            PathCreationEntity pathComponents = helper.pathSplitter(zipFile);
            List<Path> paths = helper.directoryCreator(pathComponents);

            File compactedDir = paths.get(0).toFile();
            File descompactedDir = paths.get(1).toFile();

            Path destPath = Files.move(zipFile.toPath(), Path.of(compactedDir.toPath() + File.separator + zipFile.getName()), ATOMIC_MOVE);

            FileStatus status = helper.unzipFile(destPath.toFile(), descompactedDir);
            if (status.equals(FileStatus.UNZIPPED)) {

                File[] descompactedList = descompactedDir.listFiles();
                for (int i = 0; i < descompactedList.length; i++) {
                    if (FileNameUtils.getExtension(descompactedList[i].getName()).equals("zip")) {
                        Files.move(descompactedList[i].toPath(), Path.of(compactedDir + descompactedList[i].getName()));
                    }
                }
            }
        }
        return null;
    }
}
