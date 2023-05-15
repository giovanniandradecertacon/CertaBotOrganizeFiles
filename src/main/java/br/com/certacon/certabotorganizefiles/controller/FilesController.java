package br.com.certacon.certabotorganizefiles.controller;


import br.com.certacon.certabotorganizefiles.entity.FilesEntity;
import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.service.FilesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FilesController {
    private final FilesService filesService;

    private final FilesRepository filesRepository;

    public FilesController(FilesService filesService, FilesRepository filesRepository) {
        this.filesService = filesService;
        this.filesRepository = filesRepository;
    }

    @PostMapping
    @Operation(description = "Cria o Bot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bot criado!", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = FilesEntity.class))}),
            @ApiResponse(responseCode = "400", description = "Informação inserida esta errada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageExceptionHandler.class))}),
            @ApiResponse(responseCode = "500", description = "Erro no servidor", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageExceptionHandler.class))})
    })
    public ResponseEntity<FilesEntity> create(@RequestBody FilesEntity entity) {
        FilesEntity bot = filesService.saveOrUpdate(entity);
        return ResponseEntity.status(HttpStatus.OK).body(bot);
    }


    @GetMapping
    @Operation(description = "Busca todos Bots")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bot(s) encontrado(s)!", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = FilesEntity.class))}),
            @ApiResponse(responseCode = "400", description = "Informação inserida está errada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageExceptionHandler.class))}),
            @ApiResponse(responseCode = "404", description = "Bot(s) não encontrado(s)", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageExceptionHandler.class))}),
            @ApiResponse(responseCode = "500", description = "Erro no servidor", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageExceptionHandler.class))})
    })
    public ResponseEntity<List<FilesEntity>> GetAllBots() {
        try {
            List<FilesEntity> botList = filesRepository.findAll();
            return ResponseEntity.status(HttpStatus.OK).body(botList);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PutMapping
    @Operation(description = "Atualiza um Bot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bot atualizado!", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = FilesEntity.class))}),
            @ApiResponse(responseCode = "400", description = "Informação inserida esta errada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageExceptionHandler.class))}),
            @ApiResponse(responseCode = "404", description = "Bot não foi encontrado", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageExceptionHandler.class))}),
            @ApiResponse(responseCode = "500", description = "Erro no servidor", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageExceptionHandler.class))})
    })
    public ResponseEntity<FilesEntity> update(@RequestBody FilesEntity entity) throws Exception {
        FilesEntity response = filesService.saveOrUpdate(entity);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(description = "Deleta um Bot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Bot deletado!", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Informação inserida esta errada",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageExceptionHandler.class))}),
            @ApiResponse(responseCode = "404", description = "Bot não foi encontrado", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageExceptionHandler.class))}),
            @ApiResponse(responseCode = "500", description = "Erro no servidor", content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = MessageExceptionHandler.class))})
    })
    public ResponseEntity delete(@PathVariable(value = "id") UUID id) throws FileNotFoundException {
        boolean entity = filesService.delete(id);
        if (entity == Boolean.FALSE) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bot não foi encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Deletado com Sucesso");
    }

}
