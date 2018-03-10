# BioNimbuzBox

O BioNimbuzBox é uma plataforma baseada em containers (Docker ou Outros) para a execução de workflows de bioinformática em nuvens federadas entregue tanto pelo modelo de Infraestrutura como Serviço - IaaS quanto Plataforma como Serviço - PaaS.

## Quick Start

### Pré-requisitos
- Docker 1.13.x ou superior (Docker for Linux, Docker Machine for Mac OSX ou Windows)
- Docker Compose
- 2GB RAM

#### Para uma instalação de desenvolvimento "all-in-one" usando o Docker Machine
```
$ docker-machine create -d virtualbox --virtualbox-memory 2048 bionimbuzbox
$ eval $(docker-machine env bionimbuzbox)
$ IP=$(docker-machine ip bionimbuzbox)
$ docker swarm init --advertise-addr $IP:2377
$ sudo echo "$IP web-ui" >> /etc/hosts
$ docker-compose -f https://raw.githubusercontent.com/bionimbuzbox/bionimbuzbox/master/docker-compose-minimal.yml up -d
```


