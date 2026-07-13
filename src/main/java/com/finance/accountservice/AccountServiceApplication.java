package com.finance.accountservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Account Service microservice. Bootstraps the
 * Spring Boot application with dual-database support (PostgreSQL for
 * transactional data, MongoDB for audit logs).
 */
@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

}
