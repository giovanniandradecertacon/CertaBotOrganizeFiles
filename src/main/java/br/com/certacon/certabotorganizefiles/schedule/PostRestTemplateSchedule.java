package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.repository.FilesRepository;
import br.com.certacon.certabotorganizefiles.service.PostRestTemplateService;
import br.com.certacon.certabotorganizefiles.vo.ArquivoEfdModelVO;
import br.com.certacon.certabotorganizefiles.vo.ArquivoEfdVO;
import br.com.certacon.certabotorganizefiles.vo.ProcessFileModelVO;
import br.com.certacon.certabotorganizefiles.vo.ProcessFileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostRestTemplateSchedule {
    private final PostRestTemplateService postRestTemplateService;
    private final FilesRepository filesRepository;
    @Value("${config.downloadPath}")
    private String downloadPath;

    @Value("${config.dockerPathDownload}")
    private String dockerPathDownload;

    public PostRestTemplateSchedule(PostRestTemplateService postRestTemplateService, FilesRepository filesRepository) {
        this.postRestTemplateService = postRestTemplateService;
        this.filesRepository = filesRepository;
    }

    @Scheduled(fixedRate = 30000, initialDelay = 90000)
    public boolean postRest() {
        List<UserFilesModel> modelList = filesRepository.findAll();
        boolean check = Boolean.FALSE;
        if (modelList.size() > 0) {
            for (int i = 0; i < modelList.size(); i++) {
                modelList.get(i).getId().toString();
                if (modelList.get(i).getStatus() == StatusFile.CREATED || modelList.get(i).getStatus() == StatusFile.UPDATED) {
                    ArquivoEfdVO arquivoEfdVO = ArquivoEfdVO.builder()
                            .clientCnpj(modelList.get(i).getCnpj())
                            .name("Certacon")
                            .build();
                    ArquivoEfdModelVO result = postRestTemplateService.enviarArquivoEfd(arquivoEfdVO).getBody();
                    if (result != null) {
                        ProcessFileVO processFileVO = ProcessFileVO.builder()
                                .id_arquivo(result.getId().toString())
                                .usuario("giovanni.andrade@certacon.com.br")
                                .senha("1")
                                .caminho_de_arquivo(modelList.get(i).getPath())
                                .caminho_de_destino_download(downloadPath)
                                .url_de_upload("http://" + modelList.get(i).getIpServer() + "/tributario")
                                .url_de_download(dockerPathDownload + modelList.get(i).getId().toString())
                                .nome_arquivo(modelList.get(i).getFileName())
                                .build();
                        ProcessFileModelVO processResult = postRestTemplateService.createProcess(processFileVO).getBody();
                        if (processResult != null) {
                            modelList.get(i).setProcessId(processResult.getId());
                            modelList.get(i).setStatus(StatusFile.UPLOADED);
                            check = Boolean.TRUE;
                        } else {
                            modelList.get(i).setStatus(StatusFile.ERROR);
                        }
                    } else {
                        modelList.get(i).setStatus(StatusFile.ERROR);
                    }
                    userFilesRepository.save(modelList.get(i));
                }
            }
        }
        return check;
    }
}
