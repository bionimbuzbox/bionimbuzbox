#!/bin/bash
mvn -f ../common/pom.xml clean install
mvn -f ../orchestration/pom.xml clean install
mvn -f ../provision/pom.xml clean install
mvn -f ../scheduling/pom.xml clean install
mvn -f ../web-ui/pom.xml clean install

docker build -t bionimbuz/provision ../provision
docker build -t bionimbuz/scheduling ../scheduling
docker build -t bionimbuz/web-ui ../web-ui
docker build -t bionimbuz/caddy:dev ../communication/caddy-dev
