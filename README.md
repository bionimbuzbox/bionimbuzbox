# BioNimbuzBox

O BioNimbuzBox é uma plataforma baseada em containers (Docker ou Outros) para a execução de workflows de bioinformática em nuvens federadas utilizando os modelos de Infraestrutura como Serviço - IaaS e Plataforma como Serviço - PaaS.

O BioNimbuzBox foi concebido para ser utilizado sob o modelo de Software como Serviço - SaaS através de um navegador web. 

Para o usuário/pesquisador de bioinformática, significa utilizar um software para execução de workflows de bioinformática sem a necessidade de instalação e configuração de nenhum recurso. 

Para o usuário/administrador de recursos de TI, a simplicidade de provisionamento de recursos de TI em diferentes provedores de nuvem, construindo uma federação de nuvens capaz de incorporar recursos entregues sob os modelos de IaaS e/ou PaaS, transpõe as dificuldades encontradas em soluções que fazem uso de apenas um único fornecedor/provedor.

## Quick Start

### Pré-requisitos
- Docker 1.13.x ou superior (Docker for Linux, **Docker Machine** for Mac OSX ou Windows)
- Docker Compose

#### Para uma instalação de desenvolvimento "all-in-one" usando o Docker Machine
```
$ docker-machine create -d virtualbox --virtualbox-memory 2048 bionimbuzbox
$ docker-machine ssh bionimbuzbox sudo sysctl -w vm.max_map_count=262144
$ eval $(docker-machine env bionimbuzbox)

$ IP=$(docker-machine ip bionimbuzbox)
$ sudo echo "$IP web-ui" >> /etc/hosts
$ docker swarm init --advertise-addr $IP:2377

$ wget https://raw.githubusercontent.com/bionimbuzbox/bionimbuzbox/master/docker-compose-minimal.yml
$ docker-compose -f docker-compose-minimal.yml up -d
```

Abra o navegador em http://web-ui/

## Build from Source

### Observações
O BioNimbuzBox utiliza um cliente docker, escrito em java, do projeto [spotify/docker-client][spotify/docker-client]. Atualmente este cliente está sendo usado através de um [fork][bionimbuzbox/docker-client] para construir o módulo [common](https://github.com/bionimbuzbox/bionimbuzbox/blob/master/common/pom.xml#L110) do BioNimbuzBox. 

Quando o  PR [#755][pr 755 spotify/docker-client] for aceito pelo projeto [spotify/docker-client][spotify/docker-client], poderemos utilizar as releases oficiais, não sendo mais necessário o [fork][bionimbuzbox/docker-client].

Dessa forma, será necessário compilar o projeto [bionimbuzbox/docker-client (fork)][bionimbuzbox/docker-client] e incluir a biblioteca gerada como dependencia do módulo [common](https://github.com/bionimbuzbox/bionimbuzbox/blob/master/common/)

[spotify/docker-client]: https://github.com/spotify/docker-client
[bionimbuzbox/docker-client]: https://github.com/bionimbuzbox/docker-client/tree/dev
[pr 755 spotify/docker-client]: https://github.com/spotify/docker-client/pull/755

### Pré-requisitos
- Docker 1.13.x ou superior (Docker for Linux, **Docker Machine** for Mac OSX ou Windows)
- Maven
- Git

#### Building Java Code and Docker Images (using Docker Machine) from Source
```
$ docker-machine create -d virtualbox --virtualbox-memory 2048 bionimbuzbox
$ eval $(docker-machine env bionimbuzbox)

$ git clone https://github.com/bionimbuzbox/bionimbuzbox.git
$ cd bionimbuzbox/bin
$ ./build-all.sh
```

## Informações Suplementares
Este projeto foi tema da dissertação de mestrado: ["Uma arquitetura baseada em containers para workflows de bioinformática em nuvens federadas"](http://repositorio.unb.br/handle/10482/30994) por Tiago Alves




