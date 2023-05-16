package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.UnzipAndZipFilesHelper;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class ZipFilesComponent {
    private final UnzipAndZipFilesHelper helper;

    public ZipFilesComponent(UnzipAndZipFilesHelper helper) {
        this.helper = helper;
    }

    public FileStatus zipFilesForUpload(File uuidDirectory) throws IOException {
        if (uuidDirectory.exists()) {
            File[] listFiles = uuidDirectory.listFiles();
            FileStatus status = null;
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].getName().startsWith("EFDS-")) {
                    PathCreationEntity creationEntity = helper.pathSplitter(uuidDirectory);
                    File destiny = new File(creationEntity.getRoot() +
                            File.separator + FileFoldersFunction.ORGANIZADOS +
                            File.separator + creationEntity.getIpServer() +
                            File.separator + creationEntity.getCnpj() +
                            File.separator + creationEntity.getYear() +
                            File.separator + FileType.EFDPadrao);
                    status = helper.zipFiles(listFiles[i], destiny);
                    log.info(status.name());
                } else if (listFiles[i].getName().startsWith("XMLS-")) {
                    PathCreationEntity creationEntity = helper.pathSplitter(uuidDirectory);
                    File destiny = new File(creationEntity.getRoot() +
                            File.separator + FileFoldersFunction.ORGANIZADOS +
                            File.separator + creationEntity.getIpServer() +
                            File.separator + creationEntity.getCnpj() +
                            File.separator + creationEntity.getYear() +
                            File.separator + FileType.NFe);
                    status = helper.zipFiles(listFiles[i], destiny);
                    log.info(status.name());
                }
            }
            return status;
        } else {
            throw new RuntimeException("Algum dos arquivos não existe");
        }
    }
}
