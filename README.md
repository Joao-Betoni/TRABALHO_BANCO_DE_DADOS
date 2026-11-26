# Cinema Central do Brasil

Trabalho acadêmico desenvolvido para a disciplina de Banco de Dados, com o objetivo de realizar a modelagem e implementação de um banco de dados relacional a partir de uma proposta predefinida.

O projeto foi desenvolvido em dupla pelos contribuidores deste repositório.

*Código disponível na branch Master

## 1. Proposta

A proposta selecionada foi **Pesquisa de satisfação ou de opinião**:

> Questões em escala (por exemplo, de 1 a 5), agrupadas por tema; respostas podem ser anônimas; os relatórios comparam temas, períodos e grupos de respondentes.

A partir dessa proposta, escolhemos o cinema nacional como temática da aplicação, dando origem ao nome **Cinema Central do Brasil**, em referência ao filme *Central do Brasil*, estrelado por Fernanda Montenegro.

A aplicação permite cadastrar usuários e filmes, criar e responder questionários de avaliação e consultar relatórios relacionados às pesquisas.

## 2. Tecnologias utilizadas

- **Backend:** Java e Spring Boot.
- **Frontend:** HTML, CSS, JavaScript e Bootstrap.
- **Banco de dados:** PostgreSQL.
- **Persistência:** Spring JDBC (`JdbcTemplate`).
- **Gerenciamento de dependências:** Maven.

O projeto segue uma arquitetura em camadas, separando as responsabilidades entre `Model`, `Controller`, `Service`, `DTO` e `DAO`, além dos arquivos estáticos do frontend.

O frontend foi desenvolvido de maneira simplificada, uma vez que o foco principal do trabalho é a modelagem e a implementação do banco de dados.

## 3. Configuração e execução

### Pré-requisitos

- Java JDK compatível com o projeto.
- PostgreSQL.
- IntelliJ IDEA ou Visual Studio Code.

### 3.1. Clonar o repositório

```bash
git clone https://github.com/Joao-Betoni/TRABALHO_BANCO_DE_DADOS.git
```

### 3.2. Configurar o banco de dados

Crie um banco de dados PostgreSQL chamado `CCB_DB`:

```sql
CREATE DATABASE "CCB_DB";
```

Acesse a pasta `database` do projeto e execute os scripts SQL na seguinte ordem:

1. `squema.sql` — Criação das tabelas e relacionamentos.
2. `carga.sql` — Inserção dos dados iniciais.
3. `relatorios.sql` — Consultas dos relatórios.

### 3.3. Configurar a conexão

No arquivo `src/main/resources/application.properties`, configure os dados de acesso ao PostgreSQL:

```properties
spring.application.name=central-do-brasil
spring.datasource.url=jdbc:postgresql://localhost:5432/CCB_DB
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA
```

### 3.4. Executar a aplicação

Abra o projeto no IntelliJ IDEA ou VS Code e execute a aplicação Spring Boot.

Alternativamente, pelo terminal do Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Após iniciar o servidor, acesse:

http://localhost:8080

## 4. Observações

O projeto foi desenvolvido para fins acadêmicos, com foco na aplicação dos conceitos de modelagem de dados, SQL, integridade referencial e integração com banco de dados.

A implementação utiliza consultas SQL explícitas por meio do `JdbcTemplate`, sem utilização de JPA ou Hibernate.
