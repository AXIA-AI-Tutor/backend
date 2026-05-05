package com.ax.avatarcoach.domain.document.repository;

import com.ax.avatarcoach.domain.document.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
}
