# 💻 Java Spring Boot Challenge API

Salve!
Desenvolvi esta API RESTful para gerenciamento de produtos, categorias e usuários com autenticação JWT.

---

## 🚀 Tecnologias Utilizadas

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Data JPA**
- **PostgreSQL**
- **JWT (Autenticação)**
- **Docker & Docker Compose**
- **Swagger/OpenAPI (springdoc 2.5.0)**
- **JUnit, Mockito** (testes)

---

## 📦 Como executar o projeto

### Requisitos

- Java 17+
- Docker & Docker Compose
- Git

### Executando com Docker

```bash
# Clone o repositório
$ git clone https://github.com/seu-usuario/nome-do-repositorio.git
$ cd nome-do-repositorio

# Construa e suba os containers
$ docker-compose up --build
```

A aplicação estará acessível em: `http://localhost:8080`

Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 🔐 Autenticação

A API usa autenticação via **JWT**. Primeiro você deve:

1. **Cadastrar um usuário** via `POST /auth/register`
2. **Realizar login** com `POST /auth/login`
3. **Usar o token JWT** no header:

```http
Authorization: Bearer seu_token_aqui
```

---

## 📌 Principais Endpoints

| Método | Endpoint         | Autenticado | Descrição                |
| ------ | ---------------- | ----------- | ------------------------ |
| POST   | `/auth/register` | Não         | Cadastro de novo usuário |
| POST   | `/auth/login`    | Não         | Login e gera token JWT   |
| POST   | `/auth/refresh`  | Sim         | Atualização do token JWT |
| GET    | `/products`      | Sim         | Lista todos os produtos  |
| POST   | `/products`      | Sim         | Cria um novo produto     |
| GET    | `/categories`    | Sim         | Lista categorias         |
| POST   | `/categories`    | Sim         | Cria uma nova categoria  |

---

## 📄 Exemplos de Requisições

### Registro de usuário

```http
POST /auth/register
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "123456"
}
```

### Login

```http
POST /auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "123456"
}
```

### Criação de produto

```http
POST /products
Authorization: Bearer seu_token
Content-Type: application/json

{
  "name": "Notebook Dell",
  "price": 3200.00,
  "categoryId": 1
}
```

---

## 🐋 Docker Compose

Verifique o arquivo `docker-compose.yml` para iniciar o banco de dados PostgreSQL com as credenciais corretas e a aplicação.

---

## 🧪 Testes

Os testes estão na pasta `src/test/java` e usam **JUnit 5** e **Mockito** para simulação de dependências.

Execute com:

```bash
mvn test
```

---

## 👤 Autor

Desenvolvido por Gabriel Vilela Carvalho de Souza.

---

## 📄 Licença

Este projeto está licenciado sob a Licença do Apache License 2.0 - veja o arquivo [LICENSE](LICENSE) para mais detalhes.

