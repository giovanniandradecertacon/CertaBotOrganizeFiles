package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.component.*;
import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class ProcessForRestSchedule {

    private final DirectoryManipulatorComponent directoryManipulatorComponent;
    private final MoveFilesComponent moveFilesComponent;
    private final UnzipFilesComponent unzipFilesComponent;
    private final OrganizeComponent organizeComponent;
    private final ZipFilesComponent zipFilesComponent;
    private final SaveFilesForRestComponent saveFilesForRestComponent;

    private final FilesRepository filesRepository;

    public ProcessForRestSchedule(DirectoryManipulatorComponent directoryManipulatorComponent, MoveFilesComponent moveFilesComponent, UnzipFilesComponent unzipFilesComponent, OrganizeComponent organizeComponent, ZipFilesComponent zipFilesComponent, SaveFilesForRestComponent saveFilesForRestComponent, FilesRepository filesRepository) {

        this.directoryManipulatorComponent = directoryManipulatorComponent;
        this.moveFilesComponent = moveFilesComponent;
        this.unzipFilesComponent = unzipFilesComponent;
        this.organizeComponent = organizeComponent;
        this.zipFilesComponent = zipFilesComponent;
        this.saveFilesForRestComponent = saveFilesForRestComponent;
        this.filesRepository = filesRepository;
    }

    @Scheduled(fixedRate = 1000, initialDelay = 1000)
    public void processFilesAndPrepareForBots() {

        List<FilesEntity> fileList = filesRepository.findAllByStatusEquals(FileStatus.CREATED);
        fileList.forEach(file -> {
            try {
                file = moveFilesComponent.moveFiles(file);
                file.setStatus(FileStatus.CREATED);

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                filesRepository.save(file);
            }
        });

        List<FilesEntity> movedFiles = filesRepository.findAllByStatusEquals(FileStatus.MOVED);
        movedFiles.forEach(movedFile -> {
            if (FileNameUtils.getExtension(movedFile.getFileName()).equals("zip") ||
                    FileNameUtils.getExtension(movedFile.getFileName()).equals("rar")) {
                try {
                    movedFile = unzipFilesComponent.moveAndUnzip(movedFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    filesRepository.save(movedFile);
                }
            } else if (FileNameUtils.getExtension(movedFile.getFileName()).equals("xml")) {
                File xmlFile = new File(movedFile.getFilePath());
                try {
                    File updatedFile = moveFilesComponent.separateCFeAndNFe(xmlFile);
                    FileUtils.forceDelete(xmlFile);
                    movedFile.setStatus(FileStatus.ZIPPED);
                    movedFile.setFilePath(updatedFile.getPath());
                    movedFile.setFileName(updatedFile.getName());
                } catch (IOException e) {
                    movedFile.setStatus(FileStatus.ERROR);
                    throw new RuntimeException(e);
                } finally {
                    filesRepository.save(movedFile);
                }
            } else if (FileNameUtils.getExtension(movedFile.getFileName()).equals("txt")) {
                File filePath = null;
                try {
                    filePath = zipFilesComponent.zipFile(new File(movedFile.getFilePath()), FileType.EFDPadrao);
                    FileUtils.forceDelete(new File(movedFile.getFilePath()));
                    movedFile.setStatus(FileStatus.ZIPPED);
                    movedFile.setFilePath(filePath.getPath());
                    movedFile.setFileName(filePath.getName());
                } catch (IOException e) {
                    movedFile.setStatus(FileStatus.ERROR);
                    throw new RuntimeException(e);
                } finally {
                    filesRepository.save(movedFile);
                }
            } else if (new File(movedFile.getFilePath()).isDirectory()) {
                try {
                    movedFile = directoryManipulatorComponent.folderExtract(movedFile);

                    FileStatus status = organizeComponent.organizeFilesForZipping(new File(movedFile.getFilePath()), movedFile.getId());
                    movedFile.setStatus(status);
                } catch (IOException e) {
                    movedFile.setStatus(FileStatus.ERROR);
                    throw new RuntimeException(e);
                } finally {
                    filesRepository.save(movedFile);
                }
            }
        });

        List<FilesEntity> extractedFiles = filesRepository.findAllByStatusEquals(FileStatus.EXTRACTED);
        extractedFiles.forEach(extractedFile -> {
            FileStatus fileStatus = null;
            try {
                fileStatus = organizeComponent.organizeFilesForZipping(new File(extractedFile.getFilePath()), extractedFile.getId());
                extractedFile.setStatus(fileStatus);
            } catch (IOException e) {
                extractedFile.setStatus(FileStatus.ERROR);
                throw new RuntimeException(e);
            } finally {
                filesRepository.save(extractedFile);
            }

        });

        List<FilesEntity> readyFiles = filesRepository.findAllByStatusEquals(FileStatus.READY);
        readyFiles.forEach(readyFile -> {
            List<File> filePath = null;
            try {
                filePath = zipFilesComponent.zipFilesForUpload(new File(readyFile.getFilePath()));
                for (int i = 0; i < filePath.size(); i++) {
                    FilesEntity entity = FilesEntity.builder()
                            .status(FileStatus.ZIPPED)
                            .fileName(filePath.get(i).getName())
                            .filePath(filePath.get(i).getPath())
                            .companyName(readyFile.getCompanyName())
                            .cnpj(readyFile.getCnpj())
                            .ipServer(readyFile.getIpServer())
                            .createdAt(new Date())
                            .build();
                    filesRepository.save(entity);
                    readyFile.setStatus(FileStatus.READED);
                }
            } catch (IOException e) {
                readyFile.setStatus(FileStatus.ERROR);
                throw new RuntimeException(e);
            } finally {
                filesRepository.save(readyFile);
            }

        });

        List<FilesEntity> zippedFiles = filesRepository.findAllByStatusEquals(FileStatus.ZIPPED);
        zippedFiles.forEach(zippedFile -> {
            File zipped = new File(zippedFile.getFilePath());
            try {
                if (zipped.getParentFile().getName().equals("NFe")) {
                    saveFilesForRestComponent.saveFilesForRestNFe(zippedFile);

                } else if (zipped.getParentFile().getName().equals("EFDPadrao")) {
                    saveFilesForRestComponent.saveFilesForRestEFDPadrao(zippedFile);

                } else if (zipped.getParentFile().getName().equals("CFe")) {
                    saveFilesForRestComponent.saveFilesForRestCFe(zippedFile);
                }

                zippedFile.setStatus(FileStatus.SAVED);
            } catch (IOException e) {
                zippedFile.setStatus(FileStatus.ERROR);
                throw new RuntimeException(e);
            } finally {
                filesRepository.save(zippedFile);
            }
        });
    }

}
