package br.com.certacon.certabotorganizefiles.entity;

import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilesEntity {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(generator = "UUIDGenerator")
    @Column(name = "file_id", nullable = false)
    @JsonProperty(value = "file_id")
    private UUID id;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "cnpj", nullable = false)
    private String cnpj;

    @Column(name = "ip_servidor", nullable = false)
    private String ipServer;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FileStatus status;
}
