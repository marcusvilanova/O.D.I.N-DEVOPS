# O.D.I.N. DevOps - Global Solution FIAP 2026/1

## 1. Descricao da solucao

O projeto O.D.I.N. DevOps faz parte da entrega da disciplina DevOps Tools & Cloud Computing da Global Solution FIAP 2026/1.

A solucao simula uma API Java com Spring Boot para controle de missoes orbitais e alertas relacionados a riscos espaciais. A proposta esta conectada ao tema da economia espacial, com foco em monitoramento orbital, analise de risco e deteccao de possiveis colisoes com detritos espaciais.

A aplicacao utiliza duas tabelas principais:

* missoes: armazena as missoes orbitais monitoradas.
* alertas: armazena alertas vinculados as missoes.

As tabelas possuem relacionamento por meio do campo missao_id.

## 2. Arquitetura macro da solucao

A solucao foi implantada em uma Maquina Virtual Linux na Azure, executando Docker e Docker Compose.

Fluxo da arquitetura:

Usuario / Professor
-> IP publico da VM Azure
-> Porta 8080
-> Container da API Java Spring Boot
-> Rede Docker odin-network
-> Container MySQL
-> Volume nomeado para persistencia dos dados

Informacoes da infraestrutura:

* Provedor de nuvem: Microsoft Azure
* Maquina virtual: vm-odin-devops
* Sistema operacional: Ubuntu Server 22.04 LTS
* IP publico: 20.116.61.147
* Aplicacao: http://20.116.61.147:8080/api/missoes

## 3. Containers da solucao

| Servico     | Container         | Porta | Funcao                    |
| ----------- | ----------------- | ----: | ------------------------- |
| API Java    | app-odin-rm558771 |  8080 | Aplicacao Spring Boot     |
| Banco MySQL | db-odin-rm558771  |  3306 | Banco de dados relacional |

Os dois containers executam na mesma rede Docker:

odin-devops_odin-network

O banco utiliza volume nomeado:

odin_mysql_data_rm558771

## 4. Tecnologias utilizadas

* Microsoft Azure
* Ubuntu Server 22.04 LTS
* Docker
* Docker Compose
* Java 17
* Spring Boot 3.3.5
* Maven
* MySQL 8.0
* GitHub

## 5. Requisitos DevOps atendidos

* Aplicacao Java conteinerizada.
* Banco de dados executando em container.
* Uso de Dockerfile.
* Uso de Docker Compose.
* Imagem personalizada da aplicacao.
* Container da aplicacao executando com usuario nao privilegiado.
* Diretorio de trabalho definido no Dockerfile.
* Variaveis de ambiente no container da aplicacao.
* Variaveis de ambiente no container do banco.
* Porta 8080 exposta para acesso a aplicacao.
* Porta 3306 exposta para acesso ao banco.
* Nome dos containers contendo o RM.
* App e banco executando na mesma rede Docker.
* Volume nomeado para persistencia do banco.
* CRUD completo na API.
* Persistencia comprovada com SELECT diretamente no container do banco.
* Logs dos dois containers exibidos no terminal.
* Acesso aos containers com docker container exec.
* Aplicacao executando em ambiente de nuvem na Azure.

## 6. Como executar o projeto

### 6.1 Clonar o repositorio

```
git clone git@github.com:marcusvilanova/O.D.I.N-DEVOPS.git
cd O.D.I.N-DEVOPS
```

### 6.2 Subir os containers em segundo plano

```
docker-compose up -d --build
```

### 6.3 Verificar os containers

```
docker-compose ps
```

Resultado esperado:

```
app-odin-rm558771   Up
db-odin-rm558771    Up (healthy)
```

## 7. Logs dos containers

### Logs do banco

```
docker container logs db-odin-rm558771
```

### Logs da aplicacao

```
docker container logs app-odin-rm558771
```

## 8. Evidencias com docker container exec

### Container da aplicacao

```
docker container exec app-odin-rm558771 pwd
docker container exec app-odin-rm558771 ls -l
docker container exec app-odin-rm558771 whoami
```

Resultado esperado no whoami da aplicacao:

```
odinapp
```

### Container do banco

```
docker container exec db-odin-rm558771 pwd
docker container exec db-odin-rm558771 ls -l /var/lib/mysql
docker container exec db-odin-rm558771 whoami
```

## 9. Banco de dados

### Verificar tabelas

```
docker container exec -i db-odin-rm558771 mysql -uodin_user -podin_pass odin_db -e "SHOW TABLES;"
```

Resultado esperado:

```
alertas
missoes
```

### SELECT na tabela de missoes

```
docker container exec -i db-odin-rm558771 mysql -uodin_user -podin_pass odin_db -e "SELECT * FROM missoes;"
```

### SELECT com relacionamento entre alertas e missoes

```
docker container exec -i db-odin-rm558771 mysql -uodin_user -podin_pass odin_db -e "SELECT a.id, a.missao_id, m.nome AS nome_missao, a.descricao, a.severidade FROM alertas a INNER JOIN missoes m ON m.id = a.missao_id;"
```

## 10. Testes da API

### Listar missoes

```
curl http://localhost:8080/api/missoes
```

### Criar missao

```
curl -X POST http://localhost:8080/api/missoes -H "Content-Type: application/json" -d '{"nome":"ODIN-VIDEO-001","objetivo":"Monitoramento orbital para demonstracao DevOps","status":"ATIVA"}'
```

### Consultar missao

```
curl http://localhost:8080/api/missoes/1
```

### Atualizar missao

```
curl -X PUT http://localhost:8080/api/missoes/1 -H "Content-Type: application/json" -d '{"nome":"ODIN-VIDEO-001","objetivo":"Monitoramento orbital atualizado para demonstracao","status":"EM_ANALISE"}'
```

### Criar alerta relacionado a missao

```
curl -X POST http://localhost:8080/api/alertas -H "Content-Type: application/json" -d '{"missao_id":"1","descricao":"Risco orbital identificado na demonstracao","severidade":"ALTA"}'
```

### Atualizar alerta

```
curl -X PUT http://localhost:8080/api/alertas/1 -H "Content-Type: application/json" -d '{"missao_id":"1","descricao":"Risco orbital atualizado apos nova analise","severidade":"CRITICA"}'
```

### Deletar alerta

```
curl -i -X DELETE http://localhost:8080/api/alertas/1
```

## 11. Volume nomeado

Comandos:

```
docker volume ls
docker volume inspect odin_mysql_data_rm558771
```

Volume utilizado:

```
odin_mysql_data_rm558771
```

Esse volume e utilizado para persistir os dados do banco MySQL.

## 12. Rede Docker

Comandos:

```
docker network ls
docker network inspect odin-devops_odin-network
```

Rede utilizada:

```
odin-devops_odin-network
```

Essa rede conecta os containers app-odin-rm558771 e db-odin-rm558771.

## 13. Acesso externo pela Azure

A API esta disponivel pelo IP publico da VM Azure:

```
http://20.116.61.147:8080/api/missoes
```

Teste:

```
curl http://20.116.61.147:8080/api/missoes
```

Esse acesso comprova que a solucao esta executando em ambiente de nuvem, e nao apenas em localhost.

## 14. Arquitetura macro

O desenho da arquitetura macro da solucao esta disponivel na pasta docs:

```
docs/arquitetura-macro-odin.svg
```

A arquitetura representa:

* Usuario acessando a API pelo IP publico da Azure.
* VM Ubuntu executando Docker.
* Container da aplicacao Java Spring Boot.
* Container do banco MySQL.
* Rede Docker compartilhada.
* Volume nomeado para persistencia dos dados.

## 15. Links da entrega

Repositorio GitHub SSH:

```
git@github.com:marcusvilanova/O.D.I.N-DEVOPS.git
```

Repositorio GitHub publico:

```
https://github.com/marcusvilanova/O.D.I.N-DEVOPS
```

Video demonstrativo:

```
Inserir link do YouTube apos a gravacao.
```

## 16. Integrantes

Nome: Marcus Vinicius Vila Nova
RM: 558771

Nome: Hebert Lopes dos Santos
RM: 563192

Nome: Nicolas Monteiro Ramiro
RM: 562380

Turma: 2TDS
Curso: Analise e Desenvolvimento de Sistemas - FIAP
