# Gateway Pagamento

## Sumário

* [Visão Geral](#visão-geral)
* [Tecnologias](#tecnologias)
* [Configuração](#configuração)
* [Execução](#execução)
* [Endpoints](#endpoints)

    * [Usuário](#usuário)
    * [Cobrança](#cobrança)
    * [Pagamento](#pagamento)
* [Autenticação JWT](#autenticação-jwt)
* [Testes e Cobertura](#testes-e-cobertura)

---

## Visão Geral

O **Gateway Pagamento** é um sistema backend de gerenciamento de cobranças e pagamentos entre usuários.

Funcionalidades principais:

* Criar cobranças entre usuários.
* Pagar cobranças usando saldo interno ou cartão.
* Cancelar cobranças (pendentes ou pagas).
* Registrar usuários e autenticar via JWT.

Arquitetura baseada em Spring Boot 3.5, JPA, WebFlux, Spring Security e integração com autorizador externo.

---

## Tecnologias

* Java 17
* Spring Boot 3.5 (Web, Security, Data JPA, Validation, WebFlux)
* PostgreSQL
* Lombok
* MapStruct
* JWT (io.jsonwebtoken)
* OpenAPI/Swagger (`springdoc-openapi`)
* Jacoco (cobertura de testes)
* Maven

---

## Configuração

1. Clone o repositório:

```
git clone <url-do-repositorio>
cd gateway-pagamento
```

2. Configure o banco de dados PostgreSQL no `application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/gateway
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
```

3. Configure JWT:

```
jwt.secret=uma_chave_secreta_aqui
jwt.expiracao=86400000  # 24 horas em ms
```

---

## Execução

1. Build e instalação:

```
mvn clean install
```

2. Rodar a aplicação:

```
mvn spring-boot:run
```

3. Swagger UI disponível em:

```
http://localhost:8080/swagger-ui/index.html
```

---

## Endpoints

### Usuário

#### Cadastrar usuário

**POST** `/usuarios`

**Request:**

```
{
  "nome": "Thyago Nunes",
  "cpf": "12345678901",
  "email": "thyago@email.com",
  "senha": "123456"
}
```

**Response:**

```
{
  "id": 1,
  "nome": "Thyago Nunes",
  "cpf": "12345678901",
  "email": "thyago@email.com",
  "funcao": "USUARIO"
}
```

#### Login

**POST** `/usuarios/login`

**Request:**

```
{
  "cpfOuEmail": "12345678901",
  "senha": "123456"
}
```

**Response:**

```
{
  "token": "<JWT_TOKEN>"
}
```

---

### Cobrança

#### Criar cobrança

**POST** `/cobrancas`

**Headers:**

```
Authorization: Bearer <JWT_TOKEN>
```

**Request:**

```
{
  "cpfDestinatario": "10987654321",
  "valor": 150.50,
  "descricao": "Pagamento de serviço"
}
```

**Response:**

```
{
  "id": 1,
  "cpfOriginador": "12345678901",
  "cpfDestinatario": "10987654321",
  "valor": 150.50,
  "descricao": "Pagamento de serviço",
  "status": "PENDENTE",
  "dataCriacao": "2025-10-23T21:00:00"
}
```

#### Listar enviadas

**GET** `/cobrancas/enviadas?status=PENDENTE`

**Response:**
Lista de cobranças enviadas pelo originador.

#### Listar recebidas

**GET** `/cobrancas/recebidas?status=PENDENTE`

**Response:**
Lista de cobranças recebidas pelo destinatário.

#### Cancelar cobrança

**POST** `/cobrancas/{id}/cancelar`

**Response:**

```
{
  "id": 1,
  "cpfOriginador": "12345678901",
  "cpfDestinatario": "10987654321",
  "valor": 150.50,
  "descricao": "Pagamento de serviço",
  "status": "CANCELADA",
  "dataCriacao": "2025-10-23T21:00:00"
}
```

---

### Pagamento

#### Pagar cobrança

**POST** `/pagamentos`

**Request:**

```
{
  "idCobranca": 1,
  "tipoPagamento": "SALDO",
  "numeroCartao": null,
  "validadeCartao": null,
  "cvv": null
}
```

**Response:**

```
{
  "idPagamento": 1,
  "idCobranca": 1,
  "status": "CONCLUIDO",
  "valor": 150.50,
  "pagador": "10987654321",
  "destinatario": "12345678901"
}
```

#### Depositar saldo

**POST** `/depositos`

**Request:**

```
{
  "valor": 500.00
}
```

**Response:**
HTTP 200 OK se autorizado.

---

## Autenticação JWT

Todos os endpoints, exceto `/usuarios/**` e Swagger, exigem o header:

```
Authorization: Bearer <token>
```

O `JwtAuthenticationFilter` valida o token e coloca o CPF do usuário autenticado no SecurityContext.

Tokens são gerados via `TokenService`:

```
String token = Jwts.builder()
                   .setSubject(usuario.getCpf())
                   .claim("email", usuario.getEmail())
                   .claim("papel", usuario.getFuncao().name())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + expiracao))
                   .signWith(SignatureAlgorithm.HS256, chaveSecreta)
                   .compact();
```

---

## Testes e Cobertura

* Testes unitários e de integração usando `spring-boot-starter-test` e `spring-security-test`.
* Cobertura via Jacoco:

```
mvn test jacoco:report
```

* Relatório gerado em: `target/site/jacoco/index.html`
