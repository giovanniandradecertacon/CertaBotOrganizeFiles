package br.com.certacon.certabotorganizefiles.helper;

import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class UnzipFilesHelper {

    public PathCreationEntity pathSplitter(File folderPath){
        try{
            String folderPathReplaced = folderPath.getPath().replace(File.separator, " ");
            String[] folderPathSplitted = folderPathReplaced.split(" ");
            String root = folderPathSplitted[0];
            String ipServer = folderPathSplitted[2];
            String cnpj = folderPathSplitted[3];
            String year = folderPathSplitted[4];

            return PathCreationEntity.builder()
                    .root(root)
                    .ipServer(ipServer)
                    .cnpj(cnpj)
                    .year(year)
                    .build();
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }

    }
    public Path pathCreator(PathCreationEntity components, FileFoldersFunction function){
        Path finalPath = Path.of(components.getRoot()
                + File.separator + function
                + File.separator + components.getIpServer()
                + File.separator + components.getCnpj()
                + File.separator + components.getYear());
        return finalPath;
    }

    public Path pathCreatorWithObrigacaoAcessoria(PathCreationEntity components, String function, FileType obrigacaoAcessoria){
        Path finalPath = Path.of(components.getRoot()
                + File.separator + function
                + File.separator + components.getIpServer()
                + File.separator + components.getCnpj()
                + File.separator + components.getYear()
                + File.separator + obrigacaoAcessoria);
        return finalPath;
    }

    public FileStatus moveFile(File fileToMove, Path destinyPath, CopyOption copyOption) {
        try {
            if(fileToMove.exists()){
                Files.move(fileToMove.toPath(), destinyPath, copyOption);
                return FileStatus.MOVED;
            }else{
                throw new FileNotFoundException("Arquivo não foi encontrado para mover");
            }

        }catch (IOException e){
            throw new RuntimeException("Algo deu errado ao mover o arquivo para o destino desejado");
        }
    }

    public FileStatus unzipFile(File zipFile, File destinyDirectory){
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
