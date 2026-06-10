package com.codeit.aws.repository;

import com.codeit.aws.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    // 전체 조회 - 최신순
    List<FileEntity> findAllByOrderByCreatedAtDesc();
}
