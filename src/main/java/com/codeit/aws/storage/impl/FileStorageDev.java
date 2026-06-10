package com.codeit.aws.storage.impl;

import com.codeit.aws.config.FileConfig;
import com.codeit.aws.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import com.fasterxml.uuid.Generators;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class FileStorageDev implements FileStorage {

    private final FileConfig fileConfig;

    @Value("${server.port:8080}")
    private int serverPort;

    @Override
    public String save(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = (original != null && original.contains("."))
                ? original.substring(original.lastIndexOf('.'))
                : "";
        String key = Generators.timeBasedEpochGenerator().generate() + ext;

        try {
            file.transferTo(fileConfig.getRootPath().resolve(key));
        } catch (IOException e) {
            throw new RuntimeException("로컬 파일 저장 실패: " + key, e);
        }
        return key;
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(fileConfig.getRootPath().resolve(storageKey));
        } catch (IOException e) {
            throw new RuntimeException("로컬 파일 삭제 실패: " + storageKey, e);
        }
    }

    @Override
    public String getUrl(String storageKey) {
        // request context 없을 때도 안전하게 동작
        return "http://localhost:" + serverPort + "/attachments/" + storageKey;
    }
}
