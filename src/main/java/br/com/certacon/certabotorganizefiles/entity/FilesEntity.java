package br.com.certacon.certabotorganizefiles.entity;

import br.com.certacon.certabotorganizefiles.utils.FileStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilesEntity {
    @Id
    @GenericGenerator(name = "UUIDGenerator", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.UUID, generator = "UUIDGenerator")
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

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private FileStatus status;
}
