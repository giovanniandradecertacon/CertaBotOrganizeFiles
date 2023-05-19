package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.component.*;
import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

@Service
public class MoveAndOrganizeFilesSchedule {
    private final DirectoryManipulatorComponent directoryManipulatorComponent;
    private final MoveFilesComponent moveFilesComponent;
    private final UnzipFilesComponent unzipFilesComponent;
    private final OrganizeComponent organizeComponent;
    private final ZipFilesComponent zipFilesComponent;
    private final SaveFilesForRestComponent saveFilesForRestComponent;

    private final FilesRepository filesRepository;

    public MoveAndOrganizeFilesSchedule(DirectoryManipulatorComponent directoryManipulatorComponent, MoveFilesComponent moveFilesComponent, UnzipFilesComponent unzipFilesComponent, OrganizeComponent organizeComponent, ZipFilesComponent zipFilesComponent, SaveFilesForRestComponent saveFilesForRestComponent, FilesRepository filesRepository) {
        this.directoryManipulatorComponent = directoryManipulatorComponent;
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
                                List<File> filePath = zipFilesComponent.zipFilesForUpload(new File(files.getFilePath()));
                                for (int i = 0; i < filePath.size(); i++) {
                                    FilesEntity entity = FilesEntity.builder()
                                            .status(FileStatus.ZIPPED)
                                            .fileName(filePath.get(i).getName())
                                            .filePath(filePath.get(i).getPath())
                                            .cnpj(files.getCnpj())
                                            .ipServer(files.getIpServer())
                                            .createdAt(new Date())
                                            .build();
                                    filesRepository.save(entity);
                                    files.setStatus(FileStatus.READED);
                                    filesRepository.save(files);
                                }
                            }

                        }

                        if (files.getStatus() == FileStatus.MOVED && new File(files.getFilePath()).isDirectory()) {
                            files = directoryManipulatorComponent.folderExtract(files);
                            filesRepository.save(files);

                            FileStatus status = organizeComponent.organizeFilesForZipping(new File(files.getFilePath()), files.getId());
                            files.setStatus(status);
                            filesRepository.save(files);
                            if (files.getStatus() == FileStatus.READY) {
                                List<File> filePath = zipFilesComponent.zipFilesForUpload(new File(files.getFilePath()));
                                for (int i = 0; i < filePath.size(); i++) {
                                    FilesEntity entity = FilesEntity.builder()
                                            .status(FileStatus.ZIPPED)
                                            .fileName(filePath.get(i).getName())
                                            .filePath(filePath.get(i).getPath())
                                            .cnpj(files.getCnpj())
                                            .ipServer(files.getIpServer())
                                            .createdAt(new Date())
                                            .build();
                                    filesRepository.save(entity);
                                    files.setStatus(FileStatus.READED);
                                    filesRepository.save(files);
                                }
                            }
                        }

                        if (files.getStatus() == FileStatus.MOVED && FileNameUtils.getExtension(Path.of(files.getFilePath())).equals("xml")
                                || FileNameUtils.getExtension(Path.of(files.getFilePath())).equals("txt")) {
                            List<File> filePath = zipFilesComponent.zipFile(new File(files.getFilePath()));
                            for (int i = 0; i < filePath.size(); i++) {
                                FilesEntity entity = FilesEntity.builder()
                                        .status(FileStatus.ZIPPED)
                                        .fileName(filePath.get(i).getName())
                                        .filePath(filePath.get(i).getPath())
                                        .cnpj(files.getCnpj())
                                        .ipServer(files.getIpServer())
                                        .createdAt(new Date())
                                        .build();
                                filesRepository.save(entity);
                                files.setStatus(FileStatus.READED);
                                filesRepository.save(files);
                            }
                        }
                    }

                    if (files.getStatus() == FileStatus.ZIPPED && new File(files.getFilePath()).getParentFile().getName().equals("EFDPadrao")) {
                        saveFilesForRestComponent.saveFilesForRestEFDPadrao(files);
                        files.setStatus(FileStatus.SAVED);
                        filesRepository.save(files);
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
