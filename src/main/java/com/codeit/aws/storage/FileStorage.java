package com.codeit.aws.storage;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorage {

    // 파일 저장 → storageKey 반환
    String save(MultipartFile file);

    // 파일 삭제
    void delete(String storageKey);

    // 접근 URL 반환 (로컬: 서버 URL / S3: PresignedUrl)
    String getUrl(String storageKey);
}
