package com.codeit.aws.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class FileResponseDto {
    private Long id;
    private String fileName;
    private String description;
    private String contentType;
    private String storageKey;
    private Long size;
    private Instant createdAt;
}
