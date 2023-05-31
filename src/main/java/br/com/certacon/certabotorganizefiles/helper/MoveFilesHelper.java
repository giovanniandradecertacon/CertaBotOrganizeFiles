package br.com.certacon.certabotorganizefiles.helper;

import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class MoveFilesHelper {

    public PathCreationEntity pathSplitter(File folderPath) throws FileNotFoundException {
        if (folderPath.exists()) {
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
        } else {
            throw new FileNotFoundException("Arquivo inexistente!");
        }
    }

    public Path pathCreatorWithObrigacaoAcessoria(PathCreationEntity components, FileFoldersFunction function, FileType obrigacaoAcessoria) {
        Path finalPath = Path.of(components.getRoot()
                + File.separator + function
                + File.separator + components.getIpServer()
                + File.separator + components.getCnpj()
                + File.separator + components.getYear()
                + File.separator + obrigacaoAcessoria
        );
        return finalPath;
    }

    public Path archivedPathCreator(PathCreationEntity components) {
        Path finalPath = Path.of(components.getRoot()
                + File.separator + FileFoldersFunction.ARQUIVADOS
                + File.separator + components.getIpServer()
                + File.separator + components.getCnpj()
                + File.separator + components.getYear()
        );
        return finalPath;
    }

    public Path pathCreatorForToOrganize(PathCreationEntity components) {
        Path finalPath = Path.of(components.getRoot()
                + File.separator + FileFoldersFunction.ORGANIZAR
                + File.separator + components.getIpServer()
                + File.separator + components.getCnpj()
                + File.separator + components.getYear()
        );
        return finalPath;
    }

    public FileStatus moveFile(File fileToMove, Path destinyPath, CopyOption copyOption) {
        try {
            if (fileToMove.exists()) {
                Files.move(fileToMove.toPath(), destinyPath, copyOption);
                return FileStatus.MOVED;
            } else {
                throw new FileNotFoundException("Arquivo não foi encontrado para mover");
            }

        } catch (IOException e) {
            throw new RuntimeException("Algo deu errado ao mover o arquivo para o destino desejado:" + "\n" + e);
        }
    }

    public String readFiles(File xmlFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(xmlFile));
        String line = new String();
        String result = null;

        while ((line = reader.readLine()) != null) {
            if (line.contains("<NFe")) {
                reader.close();
                result = "NFe";
                break;

            } else if (line.contains("<CFe>")) {
                reader.close();
                result = "CFe";
                break;
            }
        }
        reader.close();
        return result;
    }

}
