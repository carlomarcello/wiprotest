# wiprotest
Repositório do back end Spring Boot para o CRUD de Produtos.

Para executar a aplicação, basta fazer o clone do repositório, importar na IDE e executar a classe principal
do Spring Boot. 
```
com.cmarcello.wiprotest.WiprotestApplication
```
Por padrão, a aplicação está utilizando a porta `8080`.

Na inicialização já são criados alguns produtos conforme arquivo `data.sql`.

## Basic Authentication
As APIs estão protegidas com Basic Authentication do Spring.
O usuário (fixo) para acesso é:
```
username: carlo
password: 1234
```

## APIs

### Incluir produto 
##### POST http://localhost:8080/api/v1/products
Exemplo do body:
```
{
    "description": "Bicicleta",
    "value": "299.99",
    "creationDate": "2020-05-01T14:35:04.230Z",
    "active": true
}
```
### Editar produto 
##### PUT http://localhost:8080/api/v1/products/{productId}
Exemplo do body:
```
{    
    "description": "Bicicleta Aro 26",
    "value": "299.99",
    "active": true
}
```
### Editar produto parcialmente (Patch) 
##### PATCH http://localhost:8080/api/v1/products/{productId}
Exemplo do body para inativar um produto:
```
[
    {
    	"op": "replace",
    	"path": "/active",
    	"value": false
    }
]
```
### Listar produtos (paginado) 
##### GET http://localhost:8080/api/v1/products?active={active}
Essa API permite listar os produtos usando paginação e opcionalmente filtrando apenas os produtos ativos ou inativos.

O header da request deve conter as seguintes entradas para a paginação:
```
pageNum: {valor}
pageSize: {valor}

Ex.:
pageNum: 0 // primeira página
pageSize: 5 // contendo 5 itens
```
Exemplos da URL:
```
http://localhost:8080/api/v1/products
http://localhost:8080/api/v1/products?active=true
http://localhost:8080/api/v1/products?active=false
```
### Obter produto por ID 
##### GET http://localhost:8080/api/v1/products/{productId}
Exemplo da URL:
```
http://localhost:8080/api/v1/products/2b5486fe-18c3-4d99-b989-d70cbc4b4629
```
