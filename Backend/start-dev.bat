@echo off

mvn clean package -DskipTests &^
docker-compose up --build