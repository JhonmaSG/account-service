# account-service

Microservicio backend desarrollado con Spring Boot orientado a la gestión de cuentas bancarias, autenticación JWT y control de acceso basado en ownership.

Proyecto enfocado en aplicar conceptos de arquitectura backend empresarial utilizando Java y Spring Ecosystem.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
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
---

## Objetivos del Proyecto

* Implementar arquitectura limpia por capas
* Aplicar buenas prácticas backend empresariales
* Dominar Spring Security y JWT
* Implementar control de acceso por ownership
* Trabajar con JPA/Hibernate y PostgreSQL
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
* **MapStruct**
* **Lombok**
* **Maven**
* **Jakarta Validation**

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
* Clean Code Practices

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

## 🌐 Endpoints Implementados

| **Módulo**       | **Método & Endpoint** | **Descripción**                   | **Roles / Permisos**                                                                |
|------------------|-----------------------|-----------------------------------|-------------------------------------------------------------------------------------|
| **Auth**         | POST `/auth/register` | Registro de usuarios              | Público (sin restricciones)                                                         |
|                  | POST `/auth/login`    | Autenticación y generación de JWT | Público (sin restricciones)                                                         |
| **Accounts**     | GET `/accounts`       | Obtiene cuentas paginadas         | **ADMIN** → ve todas las cuentas<br>**USER** → ve únicamente sus cuentas            |
|                  | GET `/accounts/{id}`  | Obtiene una cuenta específica     | **ADMIN** → puede ver cualquier cuenta<br>**USER** → solo puede ver cuentas propias |
|                  | POST `/accounts`      | Crea una cuenta bancaria          | **Solo ADMIN**                                                                      |
| **Transactions** | POST `/transactions`  | Crea depósitos o retiros          | **USER → únicamente sobre cuentas propias<br>**ADMIN → acceso completo**            |


---

## 📚 Estado Actual del Proyecto

Actualmente el proyecto implementa:

* JWT Authentication
* Role-Based Authorization
* Ownership Authorization
* CRUD de cuentas
* Gestión de transacciones
* DTO Mapping
* Exception Handling
* Pagination
* Integration Testing
* Security Testing
* Transaction Validation
