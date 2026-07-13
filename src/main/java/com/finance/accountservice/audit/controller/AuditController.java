package com.finance.accountservice.audit.controller;

import com.finance.accountservice.audit.entity.AuditLog;
import com.finance.accountservice.audit.repository.AuditLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Audit", description = "ADMIN-only endpoints for querying audit logs")
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditLogRepository auditLogRepository;

    @Operation(summary = "Get all audit logs", description = "Returns all audit events. ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Audit logs retrieved")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(
                auditLogRepository.findAll()
        );
    }

    @Operation(summary = "Get logs by user", description = "Returns audit events filtered by username. ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Audit logs retrieved")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/logs/user/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getLogsByUser(
            @PathVariable String username) {
        return ResponseEntity.ok(
                auditLogRepository.findByPerformedByOrderByTimestampDesc(username)
        );
    }

    @Operation(summary = "Get logs by action", description = "Returns audit events filtered by action type. ADMIN only.")
    @ApiResponse(responseCode = "200", description = "Audit logs retrieved")
    @ApiResponse(responseCode = "403", description = "Access denied")
    @GetMapping("/logs/action/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLog>> getLogsByAction(
            @PathVariable String action) {
        return ResponseEntity.ok(
                auditLogRepository.findByActionOrderByTimestampDesc(action)
        );
    }
}