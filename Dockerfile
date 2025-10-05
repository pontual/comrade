# Etapa 1: build
FROM maven:3.9.9-amazoncorretto-21 AS build
WORKDIR /app

# copia as dependências
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# gera o código
COPY src ./src
RUN mvn -q -DskipTests package

# Etapa 2: runtime
FROM amazoncorretto:21-alpine
WORKDIR /app

# copia o jar gerado na etapa de build
COPY --from=build /app/target/*.jar app.jar

# Render injeta $PORT;
ENV JAVA_TOOL_OPTIONS="-Dserver.port=${PORT} -Xms256m -Xmx512m -XX:+UseG1GC"
EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
