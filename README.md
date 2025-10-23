## Visão Geral
O **Gateway Pagamento** é um sistema backend de gerenciamento de cobranças e pagamentos entre usuários.

Funcionalidades principais:
- Criar cobranças entre usuários.
- Pagar cobranças usando saldo interno ou cartão.
- Cancelar cobranças (pendentes ou pagas).
- Registrar usuários e autenticar via JWT.

Arquitetura baseada em Spring Boot 3.5, JPA, WebFlux, Spring Security e integração com autorizador externo.

---

## Tecnologias
- Java 17
- Spring Boot 3.5 (Web, Security, Data JPA, Validation, WebFlux)
- PostgreSQL
- Lombok
- MapStruct
- JWT (io.jsonwebtoken)
- OpenAPI/Swagger (`springdoc-openapi`)
- Jacoco (cobertura de testes)
- Maven

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
`Authorization: Bearer <JWT_TOKEN>`

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
