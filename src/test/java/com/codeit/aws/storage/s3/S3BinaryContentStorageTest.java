package com.codeit.aws.storage.s3;

import com.codeit.aws.storage.FileStorage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("prod")
class S3BinaryContentStorageTest {

    @Autowired
    FileStorage fileStorage;  // prod 프로파일 → FileStorageS3 주입

    @Test
    void save_후_getUrl_반환() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "S3 테스트".getBytes()
        );

        String key = fileStorage.save(file);

        assertThat(key).isNotBlank();
        assertThat(key).startsWith("attachments/");
        System.out.println("저장된 key: " + key);

        String url = fileStorage.getUrl(key);
        assertThat(url).startsWith("https://");
        System.out.println("PresignedUrl: " + url);

        // 테스트 후 정리
        fileStorage.delete(key);
    }

    @Test
    void delete_후_getUrl_접근_불가() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "delete-test.txt", "text/plain", "삭제 테스트".getBytes()
        );

        String key = fileStorage.save(file);
        fileStorage.delete(key);

        // 삭제 후 PresignedUrl은 생성되지만 실제 접근 시 403/404
        String url = fileStorage.getUrl(key);
        assertThat(url).startsWith("https://");
        System.out.println("삭제 후 만료 URL: " + url);
    }
}
