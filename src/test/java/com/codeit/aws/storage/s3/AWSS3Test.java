package com.codeit.aws.storage.s3;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presign.S3Presigner;
import software.amazon.awssdk.services.s3.presign.GetObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)  // 업로드 → 다운로드 순서 보장
class AWSS3Test {

    static S3Client s3Client;
    static S3Presigner s3Presigner;
    static String bucket;
    static final String TEST_KEY = "test/hello.txt";

    @BeforeAll
    static void setup() throws IOException {
        Properties props = new Properties();
        props.load(Files.newInputStream(Path.of(".env")));

        String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        String region    = props.getProperty("AWS_S3_REGION");
        bucket           = props.getProperty("AWS_S3_BUCKET");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        StaticCredentialsProvider provider = StaticCredentialsProvider.create(credentials);

        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();

        s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(provider)
                .build();
    }

    @Test
    @Order(1)
    void 업로드() {
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(TEST_KEY)
                .contentType("text/plain")
                .build();

        PutObjectResponse response = s3Client.putObject(putReq, RequestBody.fromBytes("Hello, S3!".getBytes()));
        assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
        System.out.println("업로드 완료: " + TEST_KEY);
    }

    @Test
    @Order(2)
    void 다운로드() throws IOException {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(TEST_KEY)
                .build();

        ResponseInputStream<GetObjectResponse> response = s3Client.getObject(getReq);
        String content = new String(response.readAllBytes());

        assertThat(content).isEqualTo("Hello, S3!");
        System.out.println("다운로드 완료 - 내용: " + content);
    }

    @Test
    @Order(3)
    void PresignedUrl_생성() {
        GetObjectRequest getReq = GetObjectRequest.builder()
                .bucket(bucket)
                .key(TEST_KEY)
                .build();

        GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getReq)
                .build();

        String url = s3Presigner.presignGetObject(presignReq).url().toExternalForm();

        assertThat(url).startsWith("https://");
        assertThat(url).contains(bucket);
        System.out.println("PresignedUrl: " + url);
    }
}
