package br.com.certacon.certabotorganizefiles.vo;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProcessCFeFileVO {
    private String url_de_download;
    private String senha;
    private String url_de_upload;
    private String caminho_de_arquivo;
    private String usuario;
    private String caminho_de_destino_download;
    private String nome_arquivo;
    private String id_arquivo;
    private String cnpj;
    private String nome_empresa;

}
