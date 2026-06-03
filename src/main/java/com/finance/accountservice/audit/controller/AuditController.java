package com.finance.accountservice.audit.controller;

import com.finance.accountservice.audit.entity.AuditLog;
import com.finance.accountservice.audit.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(
                auditLogRepository.findAll()
        );
    }

    @GetMapping("/logs/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getLogsByUser(
            @PathVariable String username) {
        return ResponseEntity.ok(
                auditLogRepository.findByPerformedByOrderByTimestampDesc(username)
        );
    }

    @GetMapping("/logs/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getLogsByAction(
            @PathVariable String action) {
        return ResponseEntity.ok(
                auditLogRepository.findByActionOrderByTimestampDesc(action)
        );
    }
}