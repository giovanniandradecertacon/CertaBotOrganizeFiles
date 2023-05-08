package service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {UnzipFilesService.class})
class UnzipFilesServiceTest {
    @Autowired
    private UnzipFilesService service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }
}