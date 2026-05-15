# account-service

Proyecto Clean CRUD Architecture con el objetivo de implementar arquitectura limpia backend empresarial.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![License](https://img.shields.io/badge/License-MIT-green)

---

## 📋 Descripción

**Account Service** es un microservicio backend para la gestión de cuentas bancarias y autenticación de usuarios utilizando JWT.

El proyecto fue desarrollado con el objetivo de aplicar conceptos fundamentales de desarrollo backend empresarial utilizando **Spring Boot 3**, arquitectura limpia por capas y buenas prácticas modernas de seguridad y persistencia.

---

## 🎯 Objetivos del Proyecto

* Implementar una arquitectura limpia y escalable
* Dominar el flujo completo: Controller → Service → Repository
* Implementar autenticación JWT y control de acceso por roles
* Trabajar con JPA/Hibernate y PostgreSQL
* Aplicar patrones como Repository Pattern y Dependency Injection
* Entender relaciones entre entidades y ownership authorization
* Aplicar validaciones y manejo centralizado de respuestas

---

## 🛠️ Tecnologías Utilizadas

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

---

## 🏗️ Arquitectura

El proyecto sigue una arquitectura por capas:

* Controller Layer
* Service Layer
* Repository Layer
* DTO Layer
* Security Layer

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

## ✨ Funcionalidades Implementadas

### 🔐 Autenticación y Seguridad

* Registro de usuarios
* Login con JWT
* Password encoding con BCrypt
* Roles USER y ADMIN
* Protección de endpoints con Spring Security
* Acceso basado en ownership de recursos

### 🏦 Gestión de Cuentas

* Creación de cuentas bancarias
* Consulta paginada de cuentas
* Consulta de cuenta por ID
* Actualización de cuentas
* Eliminación de cuentas
* Generación automática de accountNumber único
* Relación Account → User

### 📌 Reglas actuales

#### ADMIN puede:

* Ver todas las cuentas
* Ver cualquier cuenta por ID
* Crear cuentas para cualquier usuario
* Actualizar cuentas
* Eliminar cuentas

#### USER puede:

* Ver únicamente sus propias cuentas
* Ver únicamente cuentas que le pertenezcan

---

## 🌐 Endpoints Implementados

| **Módulo**   | **Método & Endpoint**       | **Descripción**                          | **Roles / Permisos**                                                                 |
|--------------|-----------------------------|------------------------------------------|--------------------------------------------------------------------------------------|
| **Auth**     | POST `/auth/register`       | Registro de usuarios                     | Público (sin restricciones)                                                           |
|              | POST `/auth/login`          | Autenticación y generación de JWT        | Público (sin restricciones)                                                           |
| **Accounts** | GET `/accounts`             | Obtiene cuentas paginadas                | **ADMIN** → ve todas las cuentas<br>**USER** → ve únicamente sus cuentas              |
|              | GET `/accounts/{id}`        | Obtiene una cuenta específica            | **ADMIN** → puede ver cualquier cuenta<br>**USER** → solo puede ver cuentas propias   |
|              | POST `/accounts`            | Crea una cuenta bancaria                 | **Solo ADMIN**                                                                        |


---

## 📚 Estado Actual del Proyecto

Actualmente el proyecto implementa:

* JWT Authentication
* Authorization por roles
* Ownership validation
* Relaciones JPA
* DTOs y MapStruct
* Validaciones
* Paginación
* Manejo de excepciones
* CRUD de cuentas
* Seguridad con Spring Security

El proyecto continuará evolucionando hacia una arquitectura basada en microservicios financieros.
