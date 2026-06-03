package com.finance.accountservice.audit.repository;

import com.finance.accountservice.audit.entity.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    List<AuditLog> findByPerformedByOrderByTimestampDesc(String username);
    List<AuditLog> findByActionOrderByTimestampDesc(String action);
}