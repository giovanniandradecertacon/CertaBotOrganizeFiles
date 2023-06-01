package br.com.certacon.certabotorganizefiles.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CFeFileModelVO {
    private UUID id;

    private Date createdAt;

    private Date updatedAt;

    private String fileName;

    private String nome_empresa;

}
