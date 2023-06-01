package br.com.certacon.certabotorganizefiles.vo;

import br.com.certacon.certabotorganizefiles.utils.FileStatus;
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
public class ProcessCFeFileModelVO {
    private UUID id;
    private String process;
    private String remoteDriverDownload;
    private String remoteDriverUpload;
    private String taskName;
    private String Registers;
    private String time;
    private String percentage;
    private String downloadPath;
    private String username;
    private String password;
    private String filePath;
    private FileStatus status;
    private String fileName;
    private Date createdAt;
    private Date updatedAt;
    private String nomeEmpresa;
    private String cnpj;
}
