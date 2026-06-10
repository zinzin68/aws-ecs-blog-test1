package com.codeit.aws.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String fileName;        // 원본 파일명

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false, unique = true, length = 500)
    private String storageKey; // S3 key or 로컬 파일명 (URL은 런타임에 생성)

    @Column(nullable = false)
    private Long size;

    @CreatedDate
    @Column(nullable = false, updatable = false,
            columnDefinition = "timestamp with time zone default now()")
    private Instant createdAt;

    public FileEntity(String fileName, String description, String contentType,
                      String storageKey, Long size) {
        this.fileName = fileName;
        this.description = description;
        this.contentType = contentType;
        this.storageKey = storageKey;
        this.size = size;
    }
}
