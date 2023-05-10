package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.component.MoveFilesComponent;
import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

@Service
public class MoveAndOrganizeFilesSchedule {
    private final MoveFilesComponent moveFilesComponent;

    private final FilesRepository filesRepository;

    public MoveAndOrganizeFilesSchedule(MoveFilesComponent moveFilesComponent, FilesRepository filesRepository) {
        this.moveFilesComponent = moveFilesComponent;
        this.filesRepository = filesRepository;
    }

    @Scheduled(fixedRate = 30000)
    public Boolean moveAndOrganizeScheduled() {
        List<FilesEntity> fileList = filesRepository.findAll();
        if (!fileList.isEmpty()) {
            fileList.forEach(files -> {
                try {
                    if (files.getStatus() == FileStatus.CREATED
                            || files.getStatus() == FileStatus.UPDATED) {
                        files = moveFilesComponent.moveFiles(new File(files.getFilePath()));
                        if (files.getStatus() == FileStatus.MOVED && FileNameUtils.getExtension(files.getFileName()).equals("rar")
                                || FileNameUtils.getExtension(files.getFileName()).equals("zip")) {
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
