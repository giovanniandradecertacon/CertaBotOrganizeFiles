package br.com.certacon.certabotorganizefiles.service;

import br.com.certacon.certabotorganizefiles.exception.BadRequestException;
import br.com.certacon.certabotorganizefiles.vo.ArquivoNFeVO;
import br.com.certacon.certabotorganizefiles.vo.NfeFileModelVO;
import br.com.certacon.certabotorganizefiles.vo.ProcessFileNFeModelVO;
import br.com.certacon.certabotorganizefiles.vo.ProcessNfeFileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostRestTemplateNFeService {

    private final String restBuilder;
    private RestTemplate restTemplate;

    public PostRestTemplateNFeService(@Value("${config.restBuilderNFe}") String restBuilder) {
        this.restBuilder = restBuilder;
        this.restTemplate = new RestTemplateBuilder().rootUri(this.restBuilder).build();
    }

    public ResponseEntity<NfeFileModelVO> enviarArquivoNFe(ArquivoNFeVO arquivoNFeVO) {
        ResponseEntity<NfeFileModelVO> resposta = null;
        try {
            // Crie um objeto HttpEntity contendo o objeto ArquivoEfdVO como corpo da solicitação POST
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ArquivoNFeVO> requestEntity = new HttpEntity<>(arquivoNFeVO, headers);
            // Faça a solicitação POST e obtenha a resposta como uma instância de ArquivoEfdVO
            resposta = restTemplate.exchange("/nfeFile", HttpMethod.POST, requestEntity, NfeFileModelVO.class);
        } catch (RuntimeException e) {
            throw new RuntimeException("Objeto não encontrado");
        }
        return resposta;
    }

    public ResponseEntity<ProcessFileNFeModelVO> createProcess(ProcessNfeFileVO processFileVO) {
        ResponseEntity<ProcessFileNFeModelVO> resposta = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ProcessNfeFileVO> requestEntity = new HttpEntity<>(processFileVO, headers);
            // Faça a solicitação POST e obtenha a resposta como uma instância de ArquivoEfdVO
            resposta = restTemplate.exchange("/processFile", HttpMethod.POST, requestEntity, ProcessFileNFeModelVO.class);
        } catch (BadRequestException e) {
            throw new BadRequestException();
        }
        return resposta;
    }

}
