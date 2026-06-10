# O.D.I.N. DevOps - Global Solution FIAP 2026/1

## 1. Descrição da solução

O projeto **O.D.I.N. DevOps** faz parte da entrega da disciplina **DevOps Tools & Cloud Computing** da Global Solution FIAP 2026/1.

A solução simula uma API Java com Spring Boot para controle de missões orbitais e alertas relacionados a riscos espaciais. A proposta está conectada ao tema da economia espacial, com foco em monitoramento orbital, análise de risco e identificação de possíveis colisões com detritos espaciais.

A aplicação utiliza duas tabelas principais:

* `missoes`: armazena as missões orbitais monitoradas.
* `alertas`: armazena alertas vinculados às missões.

As tabelas possuem relacionamento por meio do campo `missao_id`.

---

## 2. Arquitetura macro da solução

A solução foi implantada em uma **Máquina Virtual Linux na Microsoft Azure**, executando **Docker** e **Docker Compose**.

A arquitetura utiliza dois containers integrados:

* Container da aplicação Java Spring Boot.
* Container do banco de dados MySQL.

O acesso externo à API é realizado pelo IP público da VM Azure.

![Arquitetura DevOps em Nuvem - O.D.I.N.](docs/arquitetura-devops-odin.png)

### Fluxo da arquitetura

```text
Usuário / Navegador
→ Internet
→ IP público da VM Azure
→ Porta 8080
→ VM Ubuntu na Azure
→ Docker Compose
→ Container da API Java Spring Boot
→ Rede Docker interna
→ Container MySQL
→ Volume nomeado para persistência dos dados
```

### Informações da infraestrutura

| Item                | Informação                              |
| ------------------- | --------------------------------------- |
| Provedor de nuvem   | Microsoft Azure                         |
| Resource Group      | `rg-odin-devops-gs`                     |
| Máquina virtual     | `vm-odin-devops`                        |
| Sistema operacional | Ubuntu Server 22.04 LTS                 |
| IP público          | `20.116.61.147`                         |
| Porta da aplicação  | `8080`                                  |
| Porta do banco      | `3306`                                  |
| URL da API          | `http://20.116.61.147:8080/api/missoes` |

---

## 3. Observação sobre localhost e execução em nuvem

Esta entrega **não foi executada localmente no computador do aluno**.

A aplicação foi implantada em uma **VM Linux na Microsoft Azure**. Portanto, quando os comandos utilizam `localhost`, eles estão sendo executados **dentro da própria VM Azure**, acessada via SSH.

Nesse contexto:

```text
localhost:8080
```

representa a porta 8080 da VM em nuvem, onde o container da aplicação está em execução.

Para comprovar o acesso externo, a API também pode ser acessada pelo IP público da Azure:

```text
http://20.116.61.147:8080/api/missoes
```

---

## 4. Containers da solução

| Serviço     | Container           | Porta | Função                    |
| ----------- | ------------------- | ----: | ------------------------- |
| API Java    | `app-odin-rm558771` |  8080 | Aplicação Spring Boot     |
| Banco MySQL | `db-odin-rm558771`  |  3306 | Banco de dados relacional |

Os dois containers executam na mesma rede Docker:

```text
odin-devops_odin-network
```

O banco utiliza volume nomeado:

```text
odin_mysql_data_rm558771
```

---

## 5. Tecnologias utilizadas

* Microsoft Azure
* Ubuntu Server 22.04 LTS
* Docker
* Docker Compose
* Java 17
* Spring Boot 3.3.5
* Maven
* MySQL 8.0
* GitHub

---

## 6. Requisitos DevOps atendidos

* Aplicação Java conteinerizada.
* Banco de dados executando em container.
* Uso de Dockerfile.
* Uso de Docker Compose.
* Imagem personalizada da aplicação.
* Container da aplicação executando com usuário não privilegiado.
* Diretório de trabalho definido no Dockerfile.
* Variáveis de ambiente no container da aplicação.
* Variáveis de ambiente no container do banco.
* Porta 8080 exposta para acesso à aplicação.
* Porta 3306 exposta para acesso ao banco.
* Nome dos containers contendo o RM.
* Aplicação e banco executando na mesma rede Docker.
* Volume nomeado para persistência do banco.
* CRUD completo na API.
* Persistência comprovada com `SELECT` diretamente no container do banco.
* Logs dos dois containers exibidos no terminal.
* Acesso aos containers com `docker container exec`.
* Aplicação executando em ambiente de nuvem na Azure.
* Acesso externo validado pelo IP público da VM.

---

## 7. Como executar o projeto

### 7.1 Clonar o repositório

```bash
git clone git@github.com:marcusvilanova/O.D.I.N-DEVOPS.git
cd O.D.I.N-DEVOPS
```

### 7.2 Subir os containers em segundo plano

```bash
docker-compose up -d --build
```

### 7.3 Verificar os containers

```bash
docker-compose ps
```

Resultado esperado:

```text
app-odin-rm558771   Up
db-odin-rm558771    Up (healthy)
```

---

## 8. Logs dos containers

### Logs do banco

```bash
docker container logs db-odin-rm558771
```

### Logs da aplicação

```bash
docker container logs app-odin-rm558771
```

---

## 9. Evidências com docker container exec

### Container da aplicação

```bash
docker container exec app-odin-rm558771 pwd
docker container exec app-odin-rm558771 ls -l
docker container exec app-odin-rm558771 whoami
```

Resultado esperado no `whoami` da aplicação:

```text
odinapp
```

### Container do banco

```bash
docker container exec db-odin-rm558771 pwd
docker container exec db-odin-rm558771 ls -l /var/lib/mysql
docker container exec db-odin-rm558771 whoami
```

---

## 10. Banco de dados

### Verificar tabelas

```bash
docker container exec -i db-odin-rm558771 mysql -uodin_user -podin_pass odin_db -e "SHOW TABLES;"
```

Resultado esperado:

```text
alertas
missoes
```

### SELECT na tabela de missões

```bash
docker container exec -i db-odin-rm558771 mysql -uodin_user -podin_pass odin_db -e "SELECT * FROM missoes;"
```

### SELECT com relacionamento entre alertas e missões

```bash
docker container exec -i db-odin-rm558771 mysql -uodin_user -podin_pass odin_db -e "SELECT a.id, a.missao_id, m.nome AS nome_missao, a.descricao, a.severidade FROM alertas a INNER JOIN missoes m ON m.id = a.missao_id;"
```

---

## 11. Testes da API

### Teste interno dentro da VM Azure

Os comandos abaixo utilizam `localhost`, mas devem ser executados dentro da VM Azure via SSH.

### Listar missões

```bash
curl http://localhost:8080/api/missoes
```

### Criar missão

```bash
curl -X POST http://localhost:8080/api/missoes -H "Content-Type: application/json" -d '{"nome":"ODIN-VIDEO-001","objetivo":"Monitoramento orbital para demonstracao DevOps","status":"ATIVA"}'
```

### Consultar missão

```bash
curl http://localhost:8080/api/missoes/1
```

### Atualizar missão

```bash
curl -X PUT http://localhost:8080/api/missoes/1 -H "Content-Type: application/json" -d '{"nome":"ODIN-VIDEO-001","objetivo":"Monitoramento orbital atualizado para demonstracao","status":"EM_ANALISE"}'
```

### Criar alerta relacionado à missão

```bash
curl -X POST http://localhost:8080/api/alertas -H "Content-Type: application/json" -d '{"missao_id":"1","descricao":"Risco orbital identificado na demonstracao","severidade":"ALTA"}'
```

### Atualizar alerta

```bash
curl -X PUT http://localhost:8080/api/alertas/1 -H "Content-Type: application/json" -d '{"missao_id":"1","descricao":"Risco orbital atualizado apos nova analise","severidade":"CRITICA"}'
```

### Deletar alerta

```bash
curl -i -X DELETE http://localhost:8080/api/alertas/1
```

---

## 12. Teste externo pelo IP público da Azure

A API está disponível pelo IP público da VM Azure:

```text
http://20.116.61.147:8080/api/missoes
```

Teste:

```bash
curl http://20.116.61.147:8080/api/missoes
```

Esse acesso comprova que a solução está executando em ambiente de nuvem e não apenas em ambiente local.

---

## 13. Volume nomeado

Comandos:

```bash
docker volume ls
docker volume inspect odin_mysql_data_rm558771
```

Volume utilizado:

```text
odin_mysql_data_rm558771
```

Esse volume é utilizado para persistir os dados do banco MySQL.

---

## 14. Rede Docker

Comandos:

```bash
docker network ls
docker network inspect odin-devops_odin-network
```

Rede utilizada:

```text
odin-devops_odin-network
```

Essa rede conecta os containers `app-odin-rm558771` e `db-odin-rm558771`.

---

## 15. Arquitetura macro

O desenho da arquitetura macro da solução está disponível na pasta `docs`:

```text
docs/arquitetura-devops-odin.png
```

A arquitetura representa:

* Usuário acessando a API pelo IP público da Azure.
* Resource Group da solução.
* VM Ubuntu executando Docker e Docker Compose.
* Container da aplicação Java Spring Boot.
* Container do banco MySQL.
* Rede Docker compartilhada.
* Volume nomeado para persistência dos dados.
* Evidências técnicas com logs, `docker exec` e `SELECT` no banco.

---

## 16. Links da entrega

Repositório GitHub SSH:

```text
git@github.com:marcusvilanova/O.D.I.N-DEVOPS.git
```

Repositório GitHub público:

```text
https://github.com/marcusvilanova/O.D.I.N-DEVOPS
```

Vídeo demonstrativo:

```text
Inserir link do YouTube após a gravação.
```

---

## 17. Integrantes

| Nome                      | RM     |
| ------------------------- | ------ |
| Marcus Vinicius Vila Nova | 558771 |
| Hebert Lopes dos Santos   | 563192 |
| Nicolas Monteiro Ramiro   | 562380 |

Turma: 2TDS
Curso: Análise e Desenvolvimento de Sistemas - FIAP
