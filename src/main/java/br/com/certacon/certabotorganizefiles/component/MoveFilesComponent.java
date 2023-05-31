package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;

@Component
@Slf4j
public class MoveFilesComponent {
    private final MoveFilesHelper helper;
    private final FilesRepository filesRepository;
    private final ZipFilesComponent zipFilesComponent;

    public MoveFilesComponent(MoveFilesHelper helper, FilesRepository filesRepository, ZipFilesComponent zipFilesComponent) {
        this.helper = helper;
        this.filesRepository = filesRepository;
        this.zipFilesComponent = zipFilesComponent;
    }

    public FilesEntity moveFiles(FilesEntity entity) throws FileNotFoundException {
        File entityFile = new File(entity.getFilePath());
        if (entityFile.exists() && entityFile.isDirectory()) {
            PathCreationEntity pathComponents = helper.pathSplitter(entityFile);
            pathComponents.setPath(entityFile.getPath());
            File[] listFiles;
            do {
                listFiles = entityFile.listFiles();
                if (listFiles.length > 0) {
                    for (int i = 0; i < listFiles.length; i++) {
                        if (FileNameUtils.getExtension(listFiles[i].getName()).equals("txt")
                                || FileNameUtils.getExtension(listFiles[i].getName()).equals("zip")
                                || FileNameUtils.getExtension(listFiles[i].getName()).equals("rar")
                                || FileNameUtils.getExtension(listFiles[i].getName()).equals("xml")
                                || listFiles[i].isDirectory()) {
                            Path organizeEFDPath = helper.pathCreatorForToOrganize(pathComponents);
                            FileStatus status = helper.moveFile(listFiles[i], Path.of(organizeEFDPath + File.separator + listFiles[i].getName()), ATOMIC_MOVE);
                            FilesEntity entityForSave = FilesEntity.builder()
                                    .fileName(listFiles[i].getName())
                                    .cnpj(entity.getCnpj())
                                    .companyName(entity.getCompanyName())
                                    .ipServer(entity.getIpServer())
                                    .createdAt(new Date())
                                    .filePath(organizeEFDPath + File.separator + listFiles[i].getName())
                                    .status(status)
                                    .build();
                            entity.setStatus(FileStatus.READED);
                            filesRepository.save(entityForSave);
                        }
                    }
                } else {
                    break;
                }
            } while (listFiles.length == 0);

            return entity;
        } else {
            throw new FileNotFoundException("Diretório não existente");
        }
    }

    public File separateCFeAndNFe(File xmlFile) throws IOException {
        String filter = helper.readFiles(xmlFile);
        File zippedFile = null;
        if (filter == null) {
            PathCreationEntity pathComponents = helper.pathSplitter(xmlFile);
            Path archivedPath = helper.archivedPathCreator(pathComponents);
            helper.moveFile(xmlFile, Path.of(archivedPath + File.separator + xmlFile.getName()), ATOMIC_MOVE);

            FilesEntity wrongFile = FilesEntity.builder()
                    .filePath(archivedPath + File.separator + xmlFile.getName())
                    .fileName(xmlFile.getName())
                    .createdAt(new Date())
                    .cnpj(pathComponents.getCnpj())
                    .status(FileStatus.ERROR)
                    .build();
            filesRepository.save(wrongFile);
        } else if (filter.equals("NFe")) {
            zippedFile = zipFilesComponent.zipFile(xmlFile, FileType.NFe);
        } else if (filter.equals("CFe")) {
            zippedFile = zipFilesComponent.zipFile(xmlFile, FileType.CFe);
        }
        return zippedFile;
    }
}
