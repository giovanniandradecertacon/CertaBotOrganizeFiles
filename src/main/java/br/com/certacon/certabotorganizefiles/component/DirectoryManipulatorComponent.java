package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.helper.UnzipAndZipFilesHelper;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class DirectoryManipulatorComponent {

    private final UnzipAndZipFilesHelper unzipAndZipFilesHelper;


    public DirectoryManipulatorComponent(UnzipAndZipFilesHelper unzipAndZipFilesHelper) {
        this.unzipAndZipFilesHelper = unzipAndZipFilesHelper;
    }

    public FilesEntity folderExtract(FilesEntity entity) throws IOException {
        File directory = new File(entity.getFilePath());
        if (directory.exists()) {
            File[] fileList = directory.listFiles();
            do {
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isDirectory()) {
                        FileStatus status = unzipAndZipFilesHelper.extractFolder(fileList[i], fileList[i].getParentFile().getParentFile());
                        fileList = directory.listFiles();
                        if (status.equals(FileStatus.MOVED)) {
                            entity.setStatus(FileStatus.MOVED_FOLDER);
                        }
                    }
                }
                fileList = directory.listFiles();
            } while (unzipAndZipFilesHelper.checkFolderExistence(fileList) == Boolean.TRUE);

            return entity;
        } else {
            throw new FileNotFoundException();
        }
    }
}
