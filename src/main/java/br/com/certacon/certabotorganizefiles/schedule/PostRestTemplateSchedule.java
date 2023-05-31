package br.com.certacon.certabotorganizefiles.schedule;

import br.com.certacon.certabotorganizefiles.entity.UserFilesEntity;
import br.com.certacon.certabotorganizefiles.repository.UserFilesRepository;
import br.com.certacon.certabotorganizefiles.service.PostRestTemplateEFDPadraoService;
import br.com.certacon.certabotorganizefiles.service.PostRestTemplateNFeService;
import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import br.com.certacon.certabotorganizefiles.vo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostRestTemplateSchedule {
    private final PostRestTemplateEFDPadraoService postRestTemplateEFDPadraoService;
    private final PostRestTemplateNFeService postRestTemplateNFeService;
    private final UserFilesRepository userFilesRepository;
    @Value("${config.downloadPath}")
    private final String downloadPath;

    @Value("${config.dockerPathDownload}")
    private final String dockerPathDownload;

    @Value("${config.usuario}")
    private final String usuario;

    @Value("${config.senha}")
    private final String senha;

    public PostRestTemplateSchedule(PostRestTemplateEFDPadraoService postRestTemplateEFDPadraoService, PostRestTemplateNFeService postRestTemplateNFeService, UserFilesRepository userFilesRepository, @Value("${config.downloadPath}") String downloadPath, @Value("${config.dockerPathDownload}") String dockerPathDownload, @Value("${config.usuario}") String usuario, @Value("${config.senha}") String senha) {
        this.postRestTemplateEFDPadraoService = postRestTemplateEFDPadraoService;
        this.postRestTemplateNFeService = postRestTemplateNFeService;
        this.userFilesRepository = userFilesRepository;
        this.downloadPath = downloadPath;
        this.dockerPathDownload = dockerPathDownload;
        this.usuario = usuario;
        this.senha = senha;
    }

    @Scheduled(fixedRate = 30000, initialDelay = 45000)
    public boolean postRest() {
        List<UserFilesEntity> modelList = userFilesRepository.findAll();
        boolean check = Boolean.FALSE;
        if (modelList.size() > 0) {
            for (int i = 0; i < modelList.size(); i++) {
                modelList.get(i).getId().toString();
                if (modelList.get(i).getStatus() == FileStatus.CREATED_EFD || modelList.get(i).getStatus() == FileStatus.UPDATED) {
                    ArquivoEfdVO arquivoEfdVO = ArquivoEfdVO.builder()
                            .clientCnpj(modelList.get(i).getCnpj())
                            .name(modelList.get(i).getCompanyName())
                            .build();
                    ArquivoEfdModelVO result = postRestTemplateEFDPadraoService.enviarArquivoEfd(arquivoEfdVO).getBody();
                    if (result != null) {
                        ProcessFileVO processFileVO = ProcessFileVO.builder()
                                .id_arquivo(result.getId().toString())
                                .usuario(usuario)
                                .senha(senha)
                                .caminho_de_arquivo(modelList.get(i).getPath())
                                .caminho_de_destino_download(downloadPath)
                                .url_de_upload("http://" + modelList.get(i).getIpServer() + "/tributario")
                                .url_de_download(dockerPathDownload + modelList.get(i).getId().toString())
                                .nome_arquivo(modelList.get(i).getFileName())
                                .build();
                        ProcessFileModelVO processResult = postRestTemplateEFDPadraoService.createProcess(processFileVO).getBody();
                        if (processResult != null) {
                            modelList.get(i).setProcessId(processResult.getId());
                            modelList.get(i).setStatus(FileStatus.UPLOADED);
                            check = Boolean.TRUE;
                        } else {
                            modelList.get(i).setStatus(FileStatus.ERROR);
                        }
                    } else {
                        modelList.get(i).setStatus(FileStatus.ERROR);
                    }
                    userFilesRepository.save(modelList.get(i));

                } else if (modelList.get(i).getStatus() == FileStatus.CREATED_NFE || modelList.get(i).getStatus() == FileStatus.UPDATED) {
                    ArquivoNFeVO arquivoNFeVO = ArquivoNFeVO.builder()
                            .fileName(modelList.get(i).getFileName())
                            .build();
                    NfeFileModelVO nfeFileModelVO = postRestTemplateNFeService.enviarArquivoNFe(arquivoNFeVO).getBody();
                    if (nfeFileModelVO != null) {
                        ProcessNfeFileVO fileVO = ProcessNfeFileVO.builder()
                                .id_arquivo(nfeFileModelVO.getId().toString())
                                .nome_empresa(modelList.get(i).getCompanyName())
                                .usuario(usuario)
                                .senha(senha)
                                .cnpj(modelList.get(i).getCnpj())
                                .caminho_de_arquivo(modelList.get(i).getPath())
                                .caminho_de_destino_download(downloadPath)
                                .url_de_upload("http://" + modelList.get(i).getIpServer() + "/tributario")
                                .url_de_download(dockerPathDownload + modelList.get(i).getId().toString())
                                .nome_arquivo(modelList.get(i).getFileName())
                                .build();
                        ProcessFileNFeModelVO result = postRestTemplateNFeService.createProcess(fileVO).getBody();
                        if (result != null) {
                            modelList.get(i).setProcessId(result.getId().toString());
                            modelList.get(i).setStatus(FileStatus.UPLOADED);
                            check = Boolean.TRUE;
                        } else {
                            modelList.get(i).setStatus(FileStatus.ERROR);
                        }
                    } else {
                        modelList.get(i).setStatus(FileStatus.ERROR);
                    }
                    userFilesRepository.save(modelList.get(i));
                }
            }
        }
        return check;
    }
}
