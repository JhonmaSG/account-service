# account-service
Proyecto Clean CRUD Architecture con el objetivo de implementar arquitectura limpia backend empresarial

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![License](https://img.shields.io/badge/License-MIT-green)

## 📋 Descripción

**Account Service** es un microservicio backend que permite la creación, consulta y administración de cuentas bancarias.

Este proyecto fue desarrollado con el objetivo de aplicar y demostrar conceptos fundamentales de desarrollo backend empresarial utilizando **Spring Boot 3**, arquitectura limpia por capas y buenas prácticas de la industria.

### 🎯 Objetivos del Proyecto

- Implementar una arquitectura limpia y escalable
- Dominar el flujo completo: Controller → Service → Repository
- Trabajar con JPA/Hibernate y PostgreSQL
- Aplicar patrones como Repository Pattern y Dependency Injection
- Entender el ciclo de vida de entidades y transacciones

## 🛠️ Tecnologías Utilizadas

- **Java 17**
- **Spring Boot 3.5.14**
- **Spring Data JPA**
- **Hibernate 6**
- **PostgreSQL**
- **Lombok**
- **Maven**
- **Validaciones con Jakarta Validation**
- **Docker Ready** (próximamente)

## 🏗️ Arquitectura

El proyecto sigue una **arquitectura por capas**:

### Principales patrones aplicados:
- Repository Pattern
- Dependency Injection
- Separation of Concerns
- Transacciones con `@Transactional`
- Uso correcto de `Optional`

## ✨ Funcionalidades Implementadas

- Creación de cuentas bancarias
- Consulta de todas las cuentas
- Búsqueda por ID
- Eliminación de cuentas
- Validación de datos
- Manejo automático de fechas y estados
- Restricciones a nivel de base de datos (email único)

## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos
- Java 17
- PostgreSQL 16
- Maven

### Pasos

1. Clonar el repositorio
2. Crear la base de datos en PostgreSQL:
   ```sql
   CREATE DATABASE account_db;