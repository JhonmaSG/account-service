package com.finance.accountservice.audit.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;

    private String action;
    private String performedBy;
    private String targetEntity;
    private String targetId;
    private String details;
    private String status;
    private LocalDateTime timestamp;
}