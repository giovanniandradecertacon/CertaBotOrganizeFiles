package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.component.MoveFilesComponent;
import br.com.certacon.certabotorganizefiles.component.UnzipFilesComponent;
import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.entity.PathCreationEntity;
import br.com.certacon.certabotorganizefiles.helper.MoveFilesHelper;
import br.com.certacon.certabotorganizefiles.helper.UnzipAndZipFilesHelper;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.utils.FileFoldersFunction;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(classes = {MoveFilesComponent.class, MoveFilesHelper.class, UnzipFilesComponent.class})
class MoveAndOrganizeFilesScheduleTest {

    @MockBean
    private FilesRepository filesRepository;

    @MockBean
    private UnzipAndZipFilesHelper helper;
    @SpyBean
    private MoveAndOrganizeFilesSchedule moveAndOrganizeFilesSchedule;

    private FilesEntity entity;

    @BeforeEach
    void setUp() {
        entity = FilesEntity.builder()
                .id(UUID.randomUUID())
                .status(FileStatus.CREATED)
                .filePath("D:\\CARREGAMENTO\\192168062\\44357085000135\\2022")
                .fileName("tempdir14686753602141918318.zip")
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("chamar o metodo moveAndOrganize Quando retornar com True")
    void shouldCallMoveAndOrganizeFilesScheduleWhenMoveAndOrganizeReturnWithTrue() throws FileNotFoundException {
        //Given
        PathCreationEntity componentsPath = PathCreationEntity.builder()
                .path("D:\\CARREGAMENTO\\192168062\\44357085000135\\2022")
                .cnpj("44357085000135")
                .ipServer("192168062")
                .root("D:")
                .year("2022")
                .build();
        List<FilesEntity> fileList = new ArrayList<>();
        fileList.add(entity);

        List<Path> pathList = new ArrayList<>();
        Path uuidPath = Path.of("D:"
                + File.separator + FileFoldersFunction.ORGANIZAR
                + File.separator + componentsPath.getIpServer()
                + File.separator + componentsPath.getCnpj()
                + File.separator + componentsPath.getYear()
                + File.separator + UUID.randomUUID());
        Path compactedDir = Path.of(uuidPath
                + File.separator + FileFoldersFunction.COMPACTADOS);
        Path descomPactedDir = Path.of(uuidPath
                + File.separator + FileFoldersFunction.DESCOMPACTADOS);

        pathList.add(compactedDir);
        pathList.add(descomPactedDir);
        //When
        BDDMockito.when(filesRepository.findAll()).thenReturn(fileList);
        BDDMockito.when(helper.pathSplitter(any(File.class))).thenReturn(componentsPath);
        BDDMockito.when(helper.directoryCreator(any(PathCreationEntity.class))).thenReturn(pathList);
        boolean actual = moveAndOrganizeFilesSchedule.moveAndOrganizeScheduled();
        //Then
        assertTrue(actual);
    }
}