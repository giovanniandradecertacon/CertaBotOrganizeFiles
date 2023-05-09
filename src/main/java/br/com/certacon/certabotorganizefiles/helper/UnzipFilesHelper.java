package br.com.certacon.certabotorganizefiles.helper;

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

@Component
public class UnzipFilesHelper {
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
