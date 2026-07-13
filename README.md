# account-service

Microservicio backend desarrollado con Spring Boot orientado a la gestión de cuentas bancarias, autenticación JWT y control de acceso basado en ownership.

Proyecto enfocado en aplicar conceptos de arquitectura backend empresarial utilizando Java y Spring Ecosystem.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-7.0-brightgreen)
![Docker](https://img.shields.io/badge/Docker-29.2.1-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## Descripción

**Account Service** es un microservicio backend para la gestión de cuentas bancarias y autenticación de usuarios utilizando JWT.

El proyecto fue desarrollado con el objetivo de aplicar conceptos fundamentales de desarrollo backend empresarial utilizando **Spring Boot 3**, arquitectura limpia por capas y buenas prácticas modernas de seguridad y persistencia.

Incluye funcionalidades como:
* Gestión de usuarios
* Autenticación JWT
* Gestión de cuentas bancarias
* Registro de transacciones
* Validaciones de ownership
* Seguridad basada en roles
* Auditoría de eventos con MongoDB
---

## Objetivos del Proyecto

* Implementar arquitectura limpia por capas
* Aplicar buenas prácticas backend empresariales
* Dominar Spring Security y JWT
* Implementar control de acceso por ownership
* Trabajar con JPA/Hibernate y PostgreSQL
* Implementar auditoría de eventos con MongoDB
* Aplicar DTOs y separación de responsabilidades
* Implementar testing unitario e integración
* Preparar la evolución hacia microservicios financieros

---

##  Tecnologías Utilizadas
### Backend
* **Java 17**
* **Spring Boot 3.5.14**
* **Spring Security**
* **JWT Authentication**
* **Spring Data JPA**
* **Hibernate 6**
* **PostgreSQL**
* **Spring Data MongoDB**
* **MongoDB 7.0**
* **MapStruct**
* **Lombok**
* **Maven**
* **Jakarta Validation**

### Infraestructura
* **Docker**

### Testing
* **JUnit 5**
* **Mockito**
* **MockMvc**
* **H2 Database**
* **Spring Boot Test**
---

## Arquitectura

El proyecto sigue una arquitectura por capas:

* Controller Layer
* Service Layer
* Repository Layer
* DTO Layer
* Security Layer
* Exception Layer

### Principales patrones aplicados

* Layered Architecture
* Repository Pattern
* Dependency Injection
* DTO Pattern
* Entity Mapping
* Bean Validation
* JWT Authentication
* Role-Based Authorization
* Ownership Authorization
* Global Exception Handling
* Transaction Management
* RESTful API Design
* JPA/Hibernate ORM
* Dual Database (PostgreSQL + MongoDB)
* Clean Code Practices

---

## Persistencia dual

El proyecto utiliza dos bases de datos con responsabilidades claramente separadas:

| Base de datos | Uso |
|---|---|
| **PostgreSQL** | Datos transaccionales: usuarios, cuentas, transacciones |
| **MongoDB** | Auditoría de eventos: registro de acciones por usuario |

---

## Funcionalidades Implementadas

### Autenticación y Seguridad

* Registro de usuarios
* Login con JWT
* Password encoding con BCrypt
* Roles USER y ADMIN
* Protección de endpoints con Spring Security
* Acceso basado en ownership de recursos

### Gestión de Cuentas

* Creación de cuentas bancarias
* Consulta paginada de cuentas
* Consulta de cuenta por ID
* Actualización de cuentas
* Eliminación de cuentas
* Generación automática de accountNumber único
* Relación Account → User

### Gestión de Transacciones

* Depósitos
* Retiros
* Validación de saldo
* Actualización automática de balance
* Relación Account → Transaction

### Auditoría de Eventos

* Registro automático de eventos en MongoDB
* Eventos auditados: LOGIN, REGISTER, CREATE_ACCOUNT, DELETE_ACCOUNT, DEPOSIT, WITHDRAW
* Trazabilidad por usuario, entidad afectada y timestamp
* Consulta de logs exclusiva para ADMIN

---

## Endpoints Implementados

| **Módulo**       | **Método & Endpoint**            | **Descripción**                          | **Roles / Permisos**                                                                |
|------------------|----------------------------------|------------------------------------------|-------------------------------------------------------------------------------------|
| **Auth**         | POST `/auth/register`            | Registro de usuarios                     | Público                                                                             |
|                  | POST `/auth/login`               | Autenticación y generación de JWT        | Público                                                                             |
| **Accounts**     | GET `/accounts`                   | Obtiene cuentas paginadas                | **ADMIN** → todas las cuentas<br>**USER** → solo sus cuentas                        |
|                  | GET `/accounts/{id}`             | Obtiene una cuenta específica            | **ADMIN** → cualquier cuenta<br>**USER** → solo cuentas propias                     |
|                  | POST `/accounts`                 | Crea una cuenta bancaria                 | Solo **ADMIN**                                                                      |
|                  | PUT `/accounts/{id}`             | Actualiza una cuenta                     | Solo **ADMIN**                                                                      |
|                  | DELETE `/accounts/{id}`          | Elimina una cuenta                       | Solo **ADMIN**                                                                      |
| **Transactions** | POST `/transactions`             | Crea depósitos o retiros                 | **USER** → cuentas propias<br>**ADMIN** → acceso completo                           |
|                  | GET `/transactions`              | Consulta transacciones paginadas         | **ADMIN** → todas<br>**USER** → solo las propias                                    |
|                  | GET `/transactions/{id}`         | Consulta una transacción por ID          | **ADMIN** → cualquiera<br>**USER** → solo las propias                               |
|                  | GET `/transactions/account/{accountId}` | Transacciones por cuenta        | **ADMIN** → cualquier cuenta<br>**USER** → solo cuentas propias                     |
| **Audit**        | GET `/audit/logs`                | Obtiene todos los logs de auditoría      | Solo **ADMIN**                                                                      |
|                  | GET `/audit/logs/user/{username}` | Logs de auditoría por usuario            | Solo **ADMIN**                                                                      |
|                  | GET `/audit/logs/action/{action}` | Logs de auditoría por tipo de acción     | Solo **ADMIN**                                                                      |

---

## Estado Actual del Proyecto

| Módulo | Estado |
|---|---|
| JWT Authentication |  Estable |
| Role-Based Authorization |  Estable |
| Ownership Authorization |  Estable |
| CRUD de cuentas |  Estable |
| Gestión de transacciones |  Estable |
| DTO Mapping con MapStruct |  Estable |
| Exception Handling |  Estable |
| Pagination |  Estable |
| Integration Testing |  Estable |
| Auditoría con MongoDB |  Estable |
| Docker |  Estable |
| Docker Compose |  Estable |