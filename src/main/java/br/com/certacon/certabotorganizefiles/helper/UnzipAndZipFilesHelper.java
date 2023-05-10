package br.com.certacon.certabotorganizefiles.helper;

import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class UnzipAndZipFilesHelper {
    public FileStatus directoryCreator(PathCreationEntity entityForCreation) {
        Path uuidPath = Path.of("D:"
                + File.separator + FileFoldersFunction.ORGANIZAR
                + File.separator + entityForCreation.getIpServer()
                + File.separator + entityForCreation.getCnpj()
                + File.separator + entityForCreation.getYear()
                + File.separator + UUID.randomUUID());
        Path compactedDir = Path.of(uuidPath
                + File.separator + FileFoldersFunction.COMPACTADOS);
        Path descomPactedDir = Path.of(uuidPath
                + File.separator + FileFoldersFunction.DESCOMPACTADOS);
        if (!uuidPath.toFile().exists()) uuidPath.toFile().mkdirs();
        if (!compactedDir.toFile().exists()) compactedDir.toFile().mkdirs();
        if (!descomPactedDir.toFile().exists()) descomPactedDir.toFile().mkdirs();

        return FileStatus.CREATEDDIRS;
    }


    public FileStatus unzipFile(File zipFile, File destinyDirectory) {
        try (ArchiveInputStream entrada = new ZipArchiveInputStream(new FileInputStream(zipFile))) {
            if (!destinyDirectory.exists()) {
                destinyDirectory.mkdirs();
            }

            ArchiveEntry entry = entrada.getNextEntry();

            while (entry != null) {
                String nomeArquivo = entry.getName();
                File arquivo = new File(destinyDirectory, nomeArquivo);

                if (entry.isDirectory()) {
                    arquivo.mkdirs();
                } else {
                    File pastaArquivo = arquivo.getParentFile();
                    if (!pastaArquivo.exists()) {
                        pastaArquivo.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(arquivo);
                    IOUtils.copy(entrada, fos);
                    fos.close();
                }

                entry = entrada.getNextEntry();
            }
            return FileStatus.UNZIPPED;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
