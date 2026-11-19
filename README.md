# Biblioteca Web

A Biblioteca Web cadastra autores e livros. Cada livro tem um autor, e um autor pode ter vários livros. A chave estrangeira `livros.autor_id` guarda esse vínculo.

## Tecnologias

| Camada | Ferramentas |
| --- | --- |
| Backend | Java 17, Spring Boot 3.2, Spring Web, Spring Data JPA, Bean Validation, Lombok e Maven |
| Frontend | Node.js 20, npm, React 18, React Router 6, Axios e react-scripts 5 |
| Banco | PostgreSQL 15 em desenvolvimento e H2 em memória nos testes |
| Testes | JUnit 5, Mockito, MockMvc e Spring Boot Test |
| Infraestrutura | Docker, Docker Compose, Nginx e GitHub Actions |

## Estrutura

| Caminho | Conteúdo |
| --- | --- |
| `backend/src/main/java/com/biblioteca/` | Entidades, repositórios e controllers |
| `backend/src/test/` | Testes dos controllers, integração e perfil H2 |
| `frontend/src/components/` | Listas e formulários de autores e livros |
| `frontend/src/services/api.js` | Cliente Axios da API |
| `docker-compose.yml` | Serviços de frontend, backend e PostgreSQL |
| `.github/workflows/ci-cd.yml` | Testes, build e teste de aceitação com Docker |
| `scripts/teste-api.sh` | Teste de aceitação com curl e Python 3 |

## Como executar

Na raiz, com Docker e Docker Compose instalados:

```sh
docker compose up -d --build
```

Abra o catálogo em [localhost:3000](http://localhost:3000). A API responde em [localhost:8080/api/autores](http://localhost:8080/api/autores). O PostgreSQL usa a porta 5432 e o banco `biblioteca_db`. As credenciais locais são `postgres` e `postgres`.

Para desenvolver fora dos containers, suba somente o banco e rode cada aplicação em um terminal:

```sh
docker compose up -d postgres
```

```sh
cd backend
mvn spring-boot:run
```

```sh
cd frontend
npm install
npm start
```

O servidor de desenvolvimento encaminha as requisições `/api` ao backend. O Nginx faz o mesmo no Docker.

## Testes

```sh
cd backend
mvn test
mvn test -Dtest='*ControllerTest'
mvn test -Dtest=IntegracaoTest
```

```sh
cd frontend
npm install
npm test -- --watchAll=false --passWithNoTests
npm run build
```

Com a aplicação em execução, rode o teste de aceitação na raiz. Ele cadastra um autor e um livro e confere o vínculo na resposta:

```sh
./scripts/teste-api.sh
```

## API

| Método | URL | Status esperados |
| --- | --- | --- |
| POST | `/api/autores`, `/api/livros` | 201 na criação |
| GET | `/api/autores`, `/api/livros` | 200, em ordem de nome ou título |
| GET | `/api/autores/{id}`, `/api/livros/{id}` | 200 ou 404 |
| PUT | `/api/autores/{id}`, `/api/livros/{id}` | 200 ou 404 |
| DELETE | `/api/autores/{id}`, `/api/livros/{id}` | 200 ou 404. O autor com livros retorna 409 |
| GET | `/api/livros/autor/{autorId}` | 200, com a lista de livros do autor |

No cadastro e na atualização de livros, envie `"autor": {"id": 1}` com o ID de um autor existente. Os erros de validação retornam 400. A API retorna 404 quando o autor informado no livro não existe.

## Divisão da dupla

| Responsável | Branch | Arquivos e atividades |
| --- | --- | --- |
| DEV 1 | `feature/autores` | Entidade, repositório, controller, testes e telas de autores |
| DEV 2 | `feature/livros` | Entidade, repositório, controller, testes e telas de livros |

A dupla compartilha a integração das rotas, o cliente Axios, o Docker, o CI e a documentação. Cada integrante revisa o Pull Request do colega no GitHub.

## Evidências da entrega

- No frontend, use `npm install --package-lock-only` para gerar o `package-lock.json` e faça commit do arquivo.
- Capture o resultado de `mvn test` e a saída de `./scripts/teste-api.sh`. O script cria dados que podem ser usados nas capturas das listas e dos formulários.
- No navegador, capture `/autores`, `/autores/novo`, `/livros` e `/livros/novo`. Abra o seletor de autor na última tela.
- No GitHub, registre os dois Pull Requests com a revisão do colega e capture a execução do workflow na aba Actions.

O ZIP inclui `biblioteca-web.bundle` com o histórico Git. Para consultar as branches em uma nova cópia, execute na pasta em que o ZIP foi extraído:

```sh
git clone biblioteca-web.bundle biblioteca-web-historico
git -C biblioteca-web-historico branch -a
```
