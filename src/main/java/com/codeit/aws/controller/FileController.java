package com.codeit.aws.controller;

import com.codeit.aws.dto.FileResponseDto;
import com.codeit.aws.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileResponseDto> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        log.info("파일 단건 업로드 요청 - filename: {}, size: {}bytes",
                file.getOriginalFilename(), file.getSize());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.upload(file, description));
    }

    @PostMapping(value = "/multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileResponseDto>> uploadMultiple(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "description", required = false) String description) {

        log.info("파일 다건 업로드 요청 - {}개", files.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.uploadMultiple(files, description));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(fileService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<FileResponseDto>> findAll() {
        return ResponseEntity.ok(fileService.findAll());
    }

    // PresignedUrl로 302 리다이렉트
    @GetMapping("/{id}/download")
    public ResponseEntity<Void> download(@PathVariable Long id) {
        String url = fileService.getDownloadUrl(id);
        log.info("파일 다운로드 리다이렉트 - id: {}", id);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("파일 삭제 요청 - id: {}", id);
        fileService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
