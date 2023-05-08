package br.com.certacon.certabotorganizefiles.helper;

import entity.PathCreationEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {UnzipFilesHelper.class})
public class UnzipFilesHelperTest {
    @Autowired
    private UnzipFilesHelper helper;

    @Test
    @DisplayName("chamar o metodo pathSplitter retornando com Sucesso")
    void shouldCallUnzipFilesHelperWhenPathSplitterWhenReturnSuccess(){
        //Given
        File folderPath = new File("D:\\Carregamento\\192168062\\44357085000135\\2022");
        String expected = "Carregamento";
        //When
        PathCreationEntity actual = helper.pathSplitter(folderPath);
        //Then
        assertEquals(expected, actual.getFunction());
    }
}
