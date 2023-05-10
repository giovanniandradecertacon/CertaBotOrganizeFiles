package br.com.certacon.certabotorganizefiles.helper;

import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = {MoveFilesHelper.class})
public class MoveFilesHelperTest {
    @Autowired
    private MoveFilesHelper helper;

    @Test
    @DisplayName("chamar o metodo pathSplitter retornando com Sucesso")
    void shouldCallUnzipFilesHelperWhenPathSplitterWhenReturnSuccess() throws FileNotFoundException {
        //Given
        File folderPath = new File("D:\\Carregamento\\192168062\\44357085000135\\2022");
        String expected = "192168062";
        //When
        PathCreationEntity actual = helper.pathSplitter(folderPath);
        //Then
        assertEquals(expected, actual.getIpServer());
    }

    @Test
    @DisplayName("chamar o metodo pathSplitter com valor null retornando com Falha")
    void shouldCallUnzipFilesHelperWhenPathSplitterWhenParameterNullReturnFail() {
        //Given
        File folderPath = new File("D:\\Carregamento\\192168062\\44357085000135\\2024");
        String expected = "Arquivo inexistente!";
        //When
        FileNotFoundException actual = assertThrows(FileNotFoundException.class, () -> helper.pathSplitter(folderPath));
        //Then
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("chamar o metodo pathCreatorWithObrigacaoAcessoria quando Retornar com Sucesso")
    void shouldCallUnzipFilesHelperWhenPathCreatorReturnWithSuccess() {
        //Given
        PathCreationEntity entity = PathCreationEntity.builder()
                .ipServer("192168062")
                .cnpj("44357085000135")
                .year("2022")
                .root("D:")
                .build();
        Path expected = Path.of("D:\\ORGANIZADOS\\192168062\\44357085000135\\2022\\EFDPadrao");
        //When
        Path actual = helper.pathCreatorWithObrigacaoAcessoria(entity, FileFoldersFunction.ORGANIZADOS, FileType.EFDPadrao);
        //Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("chamar o metodo PathCreatorForToOrganize quando Retornar com Sucesso")
    void shouldCallUnzipFilesHelperWhenPathCreatorForToOrganizeReturnWithSuccess() {

    }
}
