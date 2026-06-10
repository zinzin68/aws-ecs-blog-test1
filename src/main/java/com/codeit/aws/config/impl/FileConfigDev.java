package com.codeit.aws.config.impl;

import com.codeit.aws.config.FileConfig;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Profile("dev")
public class FileConfigDev implements FileConfig {

    private Path rootPath;

    @PostConstruct
    public void init() throws IOException {
        rootPath = Paths.get(".aws-test/storage");
        Files.createDirectories(rootPath);
    }

    @Override
    public Path getRootPath() {
        return rootPath;
    }
}
