# 📊 Pontual Monitor

Sistema de monitoramento e visualização de dados para a **Pontual Telemetria e Serviços**.

---

## 🚀 Tecnologias

- **Java 21 (Amazon Corretto)**
- **Spring Boot 3**
- **PostgreSQL 17**
- **Docker + Docker Compose**
- **Flyway (para versionamento do banco)**
- **JWT (para autenticação e autorização)**

---

## 🏗️ Estrutura do Projeto
```bash

pontual-monitor-api/
├── Dockerfile
├── docker-compose.yml
├── docker-compose.override.yml
├── .env
├── mvnw
├── mvnw.cmd
├── pom.xml
├── target/
│ └── pontual-monitor-api-0.0.1-SNAPSHOT.jar
└── src/

```

---

## 🐳 Como Rodar o Projeto

### 1️⃣ Pré-requisitos

- Docker + Docker Compose instalados
- Java 21 (se quiser rodar sem Docker)

---

### 2️⃣ Configurar variáveis de ambiente

Crie um arquivo `.env` (ou use o exemplo abaixo):

POSTGRES_USER=UserPontual
POSTGRES_PASSWORD=123456
POSTGRES_DB=pontualdb

---

### 3️⃣ Compilar o Projeto

Gere o `.jar` usando o Maven Wrapper:

```bash
./mvnw clean package
