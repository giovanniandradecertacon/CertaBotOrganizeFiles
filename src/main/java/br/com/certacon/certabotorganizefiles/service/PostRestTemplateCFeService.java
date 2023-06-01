package br.com.certacon.certabotorganizefiles.service;

import br.com.certacon.certabotorganizefiles.exception.BadRequestException;
import br.com.certacon.certabotorganizefiles.vo.ArquivoCFeVO;
import br.com.certacon.certabotorganizefiles.vo.CFeFileModelVO;
import br.com.certacon.certabotorganizefiles.vo.ProcessCFeFileModelVO;
import br.com.certacon.certabotorganizefiles.vo.ProcessCFeFileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostRestTemplateCFeService {
    private final String restBuilder;
    private RestTemplate restTemplate;

    public PostRestTemplateCFeService(@Value("${config.restBuilderCFe}") String restBuilder) {
        this.restBuilder = restBuilder;
        this.restTemplate = new RestTemplateBuilder().rootUri(this.restBuilder).build();
    }

    public ResponseEntity<CFeFileModelVO> enviarArquivoCFe(ArquivoCFeVO arquivoCFeVO) {
        ResponseEntity<CFeFileModelVO> resposta = null;
        try {
            // Crie um objeto HttpEntity contendo o objeto ArquivoEfdVO como corpo da solicitação POST
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ArquivoCFeVO> requestEntity = new HttpEntity<>(arquivoCFeVO, headers);
            // Faça a solicitação POST e obtenha a resposta como uma instância de ArquivoEfdVO
            resposta = restTemplate.exchange("/cfeFile", HttpMethod.POST, requestEntity, CFeFileModelVO.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Objeto não encontrado");
        }
        return resposta;
    }

    public ResponseEntity<ProcessCFeFileModelVO> createProcess(ProcessCFeFileVO processFileVO) {
        ResponseEntity<ProcessCFeFileModelVO> resposta = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ProcessCFeFileVO> requestEntity = new HttpEntity<>(processFileVO, headers);
            // Faça a solicitação POST e obtenha a resposta como uma instância de ArquivoEfdVO
            resposta = restTemplate.exchange("/processFile", HttpMethod.POST, requestEntity, ProcessCFeFileModelVO.class);
        } catch (BadRequestException e) {
            throw new BadRequestException();
        }
        return resposta;
    }
}
