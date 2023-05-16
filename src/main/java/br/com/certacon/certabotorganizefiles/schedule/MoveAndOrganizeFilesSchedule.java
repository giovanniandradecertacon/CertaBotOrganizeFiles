package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.component.*;
import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.UserFilesEntity;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class MoveAndOrganizeFilesSchedule {
    private final MoveFilesComponent moveFilesComponent;
    private final UnzipFilesComponent unzipFilesComponent;
    private final OrganizeComponent organizeComponent;
    private final ZipFilesComponent zipFilesComponent;
    private final SaveFilesForRestComponent saveFilesForRestComponent;

    private final FilesRepository filesRepository;

    public MoveAndOrganizeFilesSchedule(MoveFilesComponent moveFilesComponent, UnzipFilesComponent unzipFilesComponent, OrganizeComponent organizeComponent, ZipFilesComponent zipFilesComponent, SaveFilesForRestComponent saveFilesForRestComponent, FilesRepository filesRepository) {
        this.moveFilesComponent = moveFilesComponent;
        this.unzipFilesComponent = unzipFilesComponent;
        this.organizeComponent = organizeComponent;
        this.zipFilesComponent = zipFilesComponent;
        this.saveFilesForRestComponent = saveFilesForRestComponent;
        this.filesRepository = filesRepository;
    }

    @Scheduled(fixedRate = 30000)
    public Boolean moveAndOrganizeScheduled() {
        List<FilesEntity> fileList = filesRepository.findAll();
        if (!fileList.isEmpty()) {
            fileList.forEach(files -> {
                try {
                    if (files.getStatus() == FileStatus.CREATED
                            || files.getStatus() == FileStatus.UPDATED && new File(files.getFilePath()).isDirectory()) {
                        files = moveFilesComponent.moveFiles(files);
                        filesRepository.save(files);
                        if (files.getStatus() == FileStatus.MOVED && FileNameUtils.getExtension(files.getFileName()).equals("rar")
                                || FileNameUtils.getExtension(files.getFileName()).equals("zip")) {
                            files = unzipFilesComponent.MoveAndUnzip(files);
                            filesRepository.save(files);
                            if (files.getStatus() == FileStatus.EXTRACTED) {
                                FileStatus fileStatus = organizeComponent.organizeFilesForZipping(new File(files.getFilePath()), files.getId());
                                files.setStatus(fileStatus);
                                filesRepository.save(files);
                            }
                            if (files.getStatus() == FileStatus.READY) {
                                Path filePath = zipFilesComponent.zipFilesForUpload(new File(files.getFilePath()));
                                files.setStatus(FileStatus.ZIPPED);
                                files.setFilePath(filePath.toString());
                                filesRepository.save(files);
                            }
                            if (files.getStatus() == FileStatus.ZIPPED) {
                                UserFilesEntity userForRest = saveFilesForRestComponent.saveFilesForRest(files);
                                userForRest.getFileName();
                            }
                        }

                    }
                } catch (FileNotFoundException e) {
                    files.setStatus(FileStatus.ERROR);
                    filesRepository.save(files);
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    files.setStatus(FileStatus.ERROR);
                    filesRepository.save(files);
                    throw new RuntimeException(e);
                }
            });

            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
