package br.com.certacon.certabotorganizefiles.service;

import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {FilesService.class})
class FilesServiceTest {
    @Autowired
    private FilesService filesService;

    @MockBean
    private FilesRepository filesRepository;

    private FilesEntity entity;

    @BeforeEach
    void setUp() {
        filesService = new FilesService(filesRepository);
        entity = FilesEntity.builder()
                .filePath("D:\\CARREGAMENTO\\192168062\\44357085000135\\2022")
                .fileName("batata.txt")
                .build();
    }

    @AfterEach
    void tearDown() {
        filesService = null;
    }

    @Test
    @DisplayName("chamar o metodo saveOrUpdate retornando com Sucesso")
    void shouldCallFilesServiceWhenSaveOrUpdateReturnWithSuccess() {
        //When
        BDDMockito.when(filesRepository.findById(any(UUID.class))).thenReturn(Optional.of(entity));
        BDDMockito.when(filesRepository.save(any(FilesEntity.class))).thenReturn(entity);
        FilesEntity actual = filesService.saveOrUpdate(entity);
        //Then
        assertEquals(entity, actual);
    }

    @Test
    @DisplayName("chamar o metodo saveOrUpdate retornando com validate Falso")
    void shouldCallFilesServiceWhenSaveOrUpdateReturnWithFalse() {
        //When
        String expected = "Nome do arquivo não pode ser nulo ou vazio";
        FilesEntity wrongEntity = FilesEntity.builder().build();
        BDDMockito.when(filesRepository.findById(any(UUID.class))).thenReturn(Optional.of(entity));
        BDDMockito.when(filesRepository.save(any(FilesEntity.class))).thenReturn(entity);
        IllegalArgumentException actual = assertThrows(IllegalArgumentException.class, () -> filesService.saveOrUpdate(wrongEntity));
        //Then
        assertEquals(expected, actual.getMessage());
    }

    @Test
    @DisplayName("chamar o metodo saveOrUpdate retornando com Sucesso para update")
    void shouldCallFilesServiceWhenSaveOrUpdateReturnWithSuccessForUpdate() {
        //Given
        FilesEntity existingEntity = FilesEntity.builder()
                .id(UUID.fromString("30356136-3034-3136-2d30-3365312d3463"))
                .fileName("batata2.txt")
                .build();
        //When
        BDDMockito.when(filesRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingEntity));
        BDDMockito.when(filesRepository.save(any(FilesEntity.class))).thenReturn(existingEntity);
        FilesEntity actual = filesService.saveOrUpdate(existingEntity);
        //Then
        assertEquals(existingEntity.getFileName(), actual.getFileName());
    }

    @Test
    @DisplayName("chamar o metodo delete quando retornar com Verdadeiro")
    void shouldCallFilesServiceWhenDeleteReturnsTrue() throws FileNotFoundException {
        //When
        FilesEntity existingEntity = FilesEntity.builder()
                .id(UUID.randomUUID())
                .fileName("batata2.txt")
                .build();
        BDDMockito.when(filesRepository.findById(any(UUID.class))).thenReturn(Optional.of(entity));
        Boolean actual = filesService.delete(existingEntity.getId());
        //Then
        assertTrue(actual);
    }

    @Test
    @DisplayName("chamar o metodo delete quando retornar com Falso")
    void shouldCallFilesServiceWhenDeleteReturnsFalse() {
        //When
        String expected = "Arquivo não existe";
        BDDMockito.when(filesRepository.findById(any(UUID.class))).thenReturn(Optional.of(entity));
        FileNotFoundException actual = assertThrows(FileNotFoundException.class, () -> filesService.delete(null));
        //Then
        assertEquals(expected, actual.getMessage());
    }
}