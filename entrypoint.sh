#!/bin/sh

# node exporter 백그라운드 실행
/usr/local/bin/node_exporter &

# Spring Boot 애플리케이션 실행
exec java -jar /app/app.jar
