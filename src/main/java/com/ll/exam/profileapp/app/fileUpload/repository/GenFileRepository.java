package com.ll.exam.profileapp.app.fileUpload.repository;

import com.ll.exam.profileapp.app.fileUpload.entity.GenFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenFileRepository extends JpaRepository<GenFile, Long> {
}
