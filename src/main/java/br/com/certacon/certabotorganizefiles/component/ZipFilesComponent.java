package br.com.certacon.certabotorganizefiles.component;

import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.UnzipAndZipFilesHelper;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ZipFilesComponent {
    private final UnzipAndZipFilesHelper helper;

    public ZipFilesComponent(UnzipAndZipFilesHelper helper) {
        this.helper = helper;
    }

    public List<File> zipFilesForUpload(File directory) throws IOException {
        if (directory.exists()) {
            List<File> destiny = new ArrayList<>();
            File[] listFiles = directory.listFiles();
            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].getName().startsWith("EFDS-")) {
                    PathCreationEntity creationEntity = helper.pathSplitter(directory);
                    File destinyEfd = new File(creationEntity.getRoot() +
                            File.separator + FileFoldersFunction.ORGANIZADOS +
                            File.separator + creationEntity.getIpServer() +
                            File.separator + creationEntity.getCnpj() +
                            File.separator + creationEntity.getYear() +
                            File.separator + FileType.EFDPadrao);
                    Path efdPath = helper.zipFiles(listFiles[i], destinyEfd);

                    destiny.add(efdPath.toFile());

                } else if (listFiles[i].getName().startsWith("XMLS-")) {
                    PathCreationEntity creationEntity = helper.pathSplitter(directory);
                    File destinyNFe = new File(creationEntity.getRoot() +
                            File.separator + FileFoldersFunction.ORGANIZADOS +
                            File.separator + creationEntity.getIpServer() +
                            File.separator + creationEntity.getCnpj() +
                            File.separator + creationEntity.getYear() +
                            File.separator + FileType.NFe);
                    Path nfePath = helper.zipFiles(listFiles[i], destinyNFe);

                    destiny.add(nfePath.toFile());
                }
            }
            FileUtils.deleteDirectory(directory);
            return destiny;
        } else {
            throw new RuntimeException("Algum dos arquivos não existe");
        }
    }

    public List<File> zipFile(File directory) throws IOException {
        List<File> destiny = new ArrayList<>();
        File[] fileList = directory.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (FileNameUtils.getExtension(fileList[i].toPath()).equals("txt")) {
                PathCreationEntity creationEntity = helper.pathSplitter(directory);
                File destinyEfd = new File(creationEntity.getRoot() +
                        File.separator + FileFoldersFunction.ORGANIZADOS +
                        File.separator + creationEntity.getIpServer() +
                        File.separator + creationEntity.getCnpj() +
                        File.separator + creationEntity.getYear() +
                        File.separator + FileType.EFDPadrao);
                Path zippedFile = helper.zipOneFile(fileList[i], destinyEfd);
                destiny.add(zippedFile.toFile());
            } else {
                PathCreationEntity creationEntity = helper.pathSplitter(directory);
                File destinyNFe = new File(creationEntity.getRoot() +
                        File.separator + FileFoldersFunction.ORGANIZADOS +
                        File.separator + creationEntity.getIpServer() +
                        File.separator + creationEntity.getCnpj() +
                        File.separator + creationEntity.getYear() +
                        File.separator + FileType.NFe);
                Path nfePath = helper.zipOneFile(fileList[i], destinyNFe);

                destiny.add(nfePath.toFile());
            }
        }
        return destiny;
    }

}
