package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.component.MoveFilesComponent;
import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {MoveFilesComponent.class, MoveFilesHelper.class})
class MoveAndOrganizeFilesScheduleTest {

    @MockBean
    private FilesRepository filesRepository;
    @SpyBean
    private MoveAndOrganizeFilesSchedule moveAndOrganizeFilesSchedule;

    private FilesEntity entity;

    @BeforeEach
    void setUp() {
        entity = FilesEntity.builder()
                .id(UUID.randomUUID())
                .filePath("D:\\CARREGAMENTO\\192168062\\44357085000135\\2022")
                .fileName("batatinha.txt")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("chamar o metodo moveAndOrganize Quando retornar com True")
    void shouldCallMoveAndOrganizeFilesScheduleWhenMoveAndOrganizeReturnWithTrue() {
        //Given
        List<FilesEntity> fileList = new ArrayList<>();
        fileList.add(entity);
        //When
        BDDMockito.when(filesRepository.findAll()).thenReturn(fileList);
        boolean actual = moveAndOrganizeFilesSchedule.moveAndOrganizeScheduled();
        //Then
        assertTrue(actual);
    }
}