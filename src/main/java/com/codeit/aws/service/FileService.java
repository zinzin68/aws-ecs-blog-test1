package com.codeit.aws.service;

import com.codeit.aws.dto.FileResponseDto;
import com.codeit.aws.entity.FileEntity;
import com.codeit.aws.repository.FileRepository;
import com.codeit.aws.storage.FileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileStorage fileStorage;
    private final FileRepository fileRepository;

    @Transactional
    public FileResponseDto upload(MultipartFile file, String description) {
        String storageKey = fileStorage.save(file);

        FileEntity entity = new FileEntity(
                file.getOriginalFilename(),
                description,
                file.getContentType(),
                storageKey,
                file.getSize()
        );
        fileRepository.save(entity);
        log.info("파일 업로드 완료 - key: {}, size: {}bytes", storageKey, file.getSize());
        return toDto(entity);
    }

    @Transactional
    public List<FileResponseDto> uploadMultiple(List<MultipartFile> files, String description) {
        if (files == null || files.isEmpty()) return List.of();

        return files.stream()
                .filter(f -> f != null && !f.isEmpty())
                .map(f -> upload(f, description))
                .toList();
    }

    @Transactional(readOnly = true)
    public FileResponseDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<FileResponseDto> findAll() {
        return fileRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .toList();
    }

    // download 엔드포인트용 - PresignedUrl or 로컬 URL 반환
    @Transactional(readOnly = true)
    public String getDownloadUrl(Long id) {
        return fileStorage.getUrl(getOrThrow(id).getStorageKey());
    }

    @Transactional
    public void delete(Long id) {
        FileEntity entity = getOrThrow(id);
        fileStorage.delete(entity.getStorageKey());
        fileRepository.delete(entity);
        log.info("파일 삭제 완료 - id: {}, key: {}", id, entity.getStorageKey());
    }

    private FileEntity getOrThrow(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다. id: " + id));
    }

    private FileResponseDto toDto(FileEntity e) {
        return new FileResponseDto(
                e.getId(), e.getFileName(), e.getDescription(),
                e.getContentType(), e.getStorageKey(), e.getSize(), e.getCreatedAt()
        );
    }
}
