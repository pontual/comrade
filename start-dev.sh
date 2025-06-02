#!/bin/bash

# Gera o JAR
echo "🔨 Compilando o projeto com Maven Wrapper..."
./mvnw clean package

# Sobe os containers
echo "🐳 Iniciando Docker Compose (backend + PostgreSQL)..."
docker compose up --build
