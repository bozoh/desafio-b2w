# Desafio B2W

## Sumário <!-- omit in toc -->

- [Desafio B2W](#desafio-b2w)
    - [Descrição](#descri%C3%A7%C3%A3o)
    - [Requisitos:](#requisitos)
    - [Funcionalidades desejadas](#funcionalidades-desejadas)
    - [Tecnologias utilizadas](#tecnologias-utilizadas)
    - [Instalação](#instala%C3%A7%C3%A3o)
        - [Baixando os arquivos da aplicação](#baixando-os-arquivos-da-aplica%C3%A7%C3%A3o)
        - [Banco de Dados mongodb](#banco-de-dados-mongodb)
            - [Carregando dados no banco de dados local (opcional)](#carregando-dados-no-banco-de-dados-local-opcional)
        - [Aplicação](#aplica%C3%A7%C3%A3o)
            - [Diretamente pelo Maven](#diretamente-pelo-maven)
            - [Gerando um executável java (JAR)](#gerando-um-execut%C3%A1vel-java-jar)
    - [API Rest](#api-rest)
        - [Adicionando um novo planeta](#adicionando-um-novo-planeta)
        - [Listando todos os planetas](#listando-todos-os-planetas)
        - [Buscando por nome](#buscando-por-nome)
        - [Buscando por ID](#buscando-por-id)
        - [Remover o planeta](#remover-o-planeta)
        - [Busca Quantidade de filmes](#busca-quantidade-de-filmes)
            - [Busca Quantidade de filmes por nome](#busca-quantidade-de-filmes-por-nome)
            - [Busca Quantidade de filmes de um planeta por id](#busca-quantidade-de-filmes-de-um-planeta-por-id)
        - [Formato do Erro da API](#formato-do-erro-da-api)
        - [Resumo da API](#resumo-da-api)

## Descrição

Criar um jogo com algumas informações da franquia.

Para possibilitar a equipe de front criar essa aplicação, queremos desenvolver uma API que contenha os dados dos planetas. 

## Requisitos:

- A API deve ser REST
- Para cada planeta, os seguintes dados devem ser obtidos do banco de dados da aplicação, sendo inserido manualmente:

    ```
    Nome
    Clima
    Terreno
    ```
- Para cada planeta também devemos ter a quantidade de aparições em filmes, que podem ser obtidas pela API pública do Star Wars:  https://swapi.co/

## Funcionalidades desejadas

- Adicionar um planeta (com nome, clima e terreno)
- Listar planetas
- Buscar por nome
- Buscar por ID
- Remover planeta

## Tecnologias utilizadas

Nessa solução foi utilizada as seguintes tecnologias:

- Java 1.8
- Mogodb 3.6
- Maven Warp 3.5
- SpringBoot 2.1.0
- Postman para os testes de integração

## Instalação

Antes da instalação se pressupõe que o java 1.8 e o git já estão instalados, se não estiverem devem ser instalados antes de continuar

### Baixando os arquivos da aplicação

Para baixar os arquivos do aplicativo deve-se usar o comando:  

`git clone https://github.com/bozoh/desafio-b2w.git`

Depois entrar na pasta do aplicativo:

`cd desafio-b2w`

Os demais comandos a seguir devem ser feito a partir da pasta do aplicativo

### Banco de Dados mongodb

O projeto atual está configurado para usar uma base remota hospedada em um servidor cloud na porta 31968, porém se por motivos de segurança isso não for possível, deve-se instalar e inicializar o mongodb e fazer as seguintes alterações no arquivo `src/main/resources/application.properties`:

Descomentar a linha com o código:  
`spring.data.mongodb.uri=mongodb://localhost:27017/desafio`

e comentar a linha com o código:  
`spring.data.mongodb.uri=mongodb://b2w:desafio-b2w@ds031968.mlab.com:31968/desafio-b2w`

Para comentar basta colocar o caractere `#` no inicio da linha

O aplicativo está considerando que o mongodb local estará na sua porta padrão (27017), se não estiver
será necessário alterar a porta na linha `spring.data.mongodb.uri`

#### Carregando dados no banco de dados local (opcional)

Para carregar uma base de dados de exemplo deve-se usar o seguinte comando:

```bash
 mongorestore -h localhost:27017 -d desafio -c planetas planetas.bson
```

### Aplicação

Para executar a aplicação recomenda-se usar uma das três formas:

#### Diretamente pelo Maven

Para executar a aplicação diretamente pelo maven use o comando:

>No linux:  
> `./mvnw clean spring-boot:run`  
>
>No windows:  
>`mvnw.cmd clean spring-boot:run`  

#### Gerando um executável java (JAR)

Para gerar um executável java use comando:

>No linux:  
> `./mvnw clean package`  
>
>No windows:  
>`mvnw.cmd clean package`  

Esse comando executa os testes do sistema antes de gerar o executável o que pode ser demorado ou até mesmo travar devido ao uso do mongodb em memória, se isso ocorrer, deve usar os seguintes comandos para gerar o executável:  

>No linux:  
> `./mvnw clean package -Dmaven.test.skip=true`  
>
>No windows:  
>`mvnw.cmd clean package -Dmaven.test.skip=true` 

Após o executável ser gerado, use o comando para inicializar o aplicativo:  

`java -jar target/desafio-0.0.1-SNAPSHOT.jar`

O aplicativo se inicializará no endereço:  

`http://127.0.0.1:8080/api/planetas`

## API Rest

### Adicionando um novo planeta

Para adicionar um novo planeta deve-se enviar um `POST` para o caminho `/api/planetas/`, com o seguinte formato:

```json
{
    "nome": "<NOME>",
    "clima": "<CLIMA>",
    "terreno": "<TERRENO>"
}
```

Todos os campos são obrigatórios e devem ter pelo menos 3 caracteres, o campo nome deve ser único, ou seja, não pode existir na base de dados e também deve ser um nome de planeta pertencente a franquia *Star Wars*.

### Listando todos os planetas

Para listar todos os planetas cadastrados deve-se enviar um `GET` para o caminho `/api/planetas/`

Pode-se, opcionalmente, controlar a paginação nas listagens, passando os parâmetros `pagina` e `tamanho` onde `pagina` é o número da pagina desejada e `tamanho` e a quantidade de elementos por página (o padrão é 10).  
Então para listar todos usando o controle de página deve-se  enviar um `GET` para o caminho `/api/planetas?pagina=<NUM. PÁGINA>&tamanho=<ÍTENS POR PÁGINA>`.


E vai retornar uma lista de planetas no formato

```json
{
    "content": [
        {"nome": "<NOME>", "clima": "<CLIMA>", "terreno": "<TERRENO>"},
        {"nome": "<NOME>", "clima": "<CLIMA>", "terreno": "<TERRENO>"},
        {"nome": "<NOME>", "clima": "<CLIMA>", "terreno": "<TERRENO>"},
        ...
    ],
    "totalElements": <QUANTIDADE TOTAL DE PLANETAS RETORNADO PELO BD>,
    "last": <true SE FOR A ÚLTIMA PÁGINA>,
    "totalPages": <QUANTIDADE DE PÁGINAS>,
    "first": <true SE FOR A PRIMEIRA PÁGINA>,
    "numberOfElements": <QUANTIDADE DE PLANETAS NA PÁGINA>,
    "size": <QUANTIDATE DE PLANETAS POR PÁGINA>,
    "number": <NÚMERO DA PÁGINA>,
    "empty": <true SE A PÁGINA NÃO TIVER PLANETAS>
}
```

Existe mais elementos na resposta de uma listagem, porém são irrelevantes para paginação desse aplicativo.  
>O  `numberOfElements` mostra a quantidade de elementos retornado da base de dados nessa página.  
>O  `size` mostra o número de itens por página, é o mesmo valor do parâmetro `tamanho`.  

### Buscando por nome

Para procurar por planetas por nome, deve-se enviar um `GET` para o caminho `/api/planetas/nome/<NOME>`, onde `<NOME>` é o nome do planeta desejado.  
Essa busca não precisa do nome completo do planeta e nem faz diferenciação de maiúsculas de minúsculas (case insensitive).

E retorna uma lista de planetas encontrados, no mesmo formato que a listagem dos planetas. Também possui o controle de paginação da mesma forma da listagem do planeta.

### Buscando por ID

Para procurar por planetas por id, deve-se enviar um `GET` para o caminho `/api/planetas/<ID>`, onde `<ID>` é o id do planeta desejado.  
E retorna o planeta desejado, no formato;

```json
{
    "id": "<ID>",
    "nome": "<NOME>",
    "clima": "<CLIMA>",
    "terreno": "<TERRENO>",
    "filmes": <QUANTIDADE DE APARIÇÕES EM FILMES>
}
```

ou um erro se não encontrado,  ver abaixo o formato do erro.

### Remover o planeta

Para remover um planeta, deve-se enviar um `DELETE` para o caminho `/api/planetas/<ID>`, onde `<ID>` é o id do planeta desejado.  
E não retorna dado algum.

### Busca Quantidade de filmes

Ainda é possível realiza a busca pela quantidade de aparições de um planeta na franquia *Star Wars* por nome ou pelo id.

#### Busca Quantidade de filmes por nome

Para buscar a quantidade de aparições de um planeta por nome, deve realizar um `GET` para o caminho `/api/planetas/nome/<NOME>/filmes`.

Onde `<NOME>` é o nome exato do planeta, só não diferenciando maiúsculas de minúsculas (case insensitive), e o nome do planeta **não** precisa estar cadastrado na base de dados.  
Retorna um número com a quantidade de aparições de um planeta, ou um erro se o planeta não for encontrado.

#### Busca Quantidade de filmes de um planeta por id

Para buscar a quantidade de aparições de um planeta por nome, deve realizar um `GET` para o caminho `/api/planetas/<ID>/filmes`

Onde `<ID>` é o id do planeta.  
Retorna um número com a quantidade de aparições de um planeta, ou um erro se o id não existir.

### Formato do Erro da API

Os erros retornados por essa API segue o seguinte formato:

```json
{
    "date": "<DATA DO ERRO NO FORMATO YYYY-MM-DD HH:MM:SS>",
    "fieldErrors": [
        {
            "field": "<NOME DO CAMPO QUE GEROU O ERRO>",
            "code": "<CÓDIGO DO ERRO>",
            "mensagem": "<MENSAGEM DE ERRO>",
            "rejectedValue": "<VALOR QUE GEROU O ERRO>"
        },
    ],
    "globalErrors": [
        {
            "code": "<CÓDIGO DO ERRO>",
            "mensagem": "<MENSAGEM DE ERRO>"
        }
    ],
    "path": "<CAMINHO CHAMADO>"
}
```

Nem todos os campos são retornados nas resposta com erros

### Resumo da API

|Ação|Caminho|Parâmetros do Request|Retorno|
|----|-------|---------------------|-------|
|Criar um planeta| `POST /api/planetas/`|`{ "nome": "<NOME>",  "clima": <CLIMA>",  "terreno": "<TERRENO>"  }`|`{ "id": <ID>,  "nome": "<NOME>",  "clima": <CLIMA>",  "terreno": "<TERRENO>",  "filmes" <NUM. FILMES> }`|
|Listar todos os planetas| GET /api/planetas/?pagina=`<NUM. PÁGINA>`&tamanho=`<ÍTENS POR PÁGINA>`||`{"content": [{ "id": <ID>,  "nome": "<NOME>",  "clima": <CLIMA>",  "terreno": "<TERRENO>",  "filmes" <NUM. FILMES> }, ... ],"totalElements": <QUANTIDADE TOTAL DE PLANETAS RETORNADO PELO BD>,  "last": <true SE FOR A ÚLTIMA PÁGINA>,     "totalPages": <QUANTIDADE DE PÁGINAS>,  "first": <true SE FOR A PRIMEIRA PÁGINA>,  "numberOfElements": <QUANTIDADE DE PLANETAS NA PÁGINA>,  "size": <QUANTIDATE DE PLANETAS POR PÁGINA>,  "number": <NÚMERO DA PÁGINA>,  "empty": <true SE A PÁGINA NÃO TIVER PLANETAS>  }`|
|Buscar planetas por nome| GET /api/planetas/nome/`<NOME>`/?pagina=`<NUM. PÁGINA>`&tamanho=`<ÍTENS POR PÁGINA>`||`{"content": [{ "id": <ID>,  "nome": "<NOME>",  "clima": <CLIMA>",  "terreno": "<TERRENO>",  "filmes" <NUM. FILMES> }, ... ],"totalElements": <QUANTIDADE TOTAL DE PLANETAS RETORNADO PELO BD>,  "last": <true SE FOR A ÚLTIMA PÁGINA>,     "totalPages": <QUANTIDADE DE PÁGINAS>,  "first": <true SE FOR A PRIMEIRA PÁGINA>,  "numberOfElements": <QUANTIDADE DE PLANETAS NA PÁGINA>,  "size": <QUANTIDATE DE PLANETAS POR PÁGINA>,  "number": <NÚMERO DA PÁGINA>,  "empty": <true SE A PÁGINA NÃO TIVER PLANETAS>  }`|
|Buscar planetas por id| GET /api/planetas/`<ID>`||`{ "id": <ID>,  "nome": "<NOME>",  "clima": <CLIMA>",  "terreno": "<TERRENO>",  "filmes" <NUM. FILMES> }`|
|Apagar um planeta| DELETE /api/planetas/`<ID>`|||
|Buscar quantidade de aparições por nome| GET /api/planetas/nome/`<NOME>`/filmes||`<NUM. DE APARIÇÕES>`|
|Buscar quantidade de aparições por id| GET /api/planetas/`<ID>`/filmes||`<NUM. DE APARIÇÕES>`|

