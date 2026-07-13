package com.finance.accountservice.audit.service;

import com.finance.accountservice.audit.entity.AuditLog;
import com.finance.accountservice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for persisting audit events to MongoDB. Logs user actions
 * such as login, registration, account creation, and financial
 * transactions for traceability.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String action, String performedBy, String targetEntity,
                    String targetId, String details, String status) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .targetEntity(targetEntity)
                .targetId(targetId)
                .details(details)
                .status(status)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
        log.info("AUDIT [{}] by {} on {}:{} -> {}", action, performedBy, targetEntity, targetId, status);
    }
}