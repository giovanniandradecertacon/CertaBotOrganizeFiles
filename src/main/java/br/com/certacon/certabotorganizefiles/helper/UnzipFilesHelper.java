package br.com.certacon.certabotorganizefiles.helper;

import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class UnzipFilesHelper {
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
}
