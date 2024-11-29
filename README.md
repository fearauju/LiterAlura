



![ChallengerLiterAlura (1)](https://github.com/user-attachments/assets/771c4ecd-1898-475d-b418-cec7a91d234f)




# **LiterAlura**

**LiterAlura** é um projeto abrangente projetado para gerenciar livros e autores, focando em insights estatísticos e funcionalidades de busca. A aplicação utiliza Spring Boot, JPA, PostgreSQL e JLine para fornecer uma experiência interativa através de uma interface baseada em terminal.

---

## **Funcionalidades**

### **1. Gerenciamento de Livros e Autores**

- **Adicionar Livros**: Adicione livros com metadados como título, idiomas, assuntos, gêneros, status de copyright e contagem de downloads.
- **Gerenciar Autores**: Associe múltiplos autores a livros e gerencie seus dados pessoais (por exemplo, ano de nascimento, ano de falecimento).

### **2. Consultas e Filtros Avançados**

- **Buscar por Idioma**: Recupere livros e autores associados filtrados por idioma.
- **Buscar por Assuntos ou Gêneros**: Encontre livros categorizados por assuntos ou gêneros específicos.
- **Domínio Público vs. Copyright**: Analise livros com base em seu status de copyright.

### **3. Estatísticas Interativas**

- **Livros por Idioma**: Exiba estatísticas sobre o número de livros por idioma e os gêneros predominantes para cada um.
- **Autores por Gênero**: Agrupe e conte autores que publicaram obras em vários gêneros.
- **Autores por Idioma**: Identifique autores multilíngues que publicaram em mais de um idioma.
- **Média de Autores por Livro**: Calcule a média, o mínimo e o máximo de autores para os livros no banco de dados.
- **Livros Mais Baixados**: Mostre os livros mais baixados por idioma ou gênero.

### **4. Interface de Terminal com JLine**

- **Sistema de Menu**: Navegue por um menu interativo para explorar estatísticas e gerenciar dados.
- **Insights em Tempo Real**: Busque e exiba dados ao vivo usando consultas otimizadas com Spring JPA.

---

## Tecnologias

- **Backend**: Java, Spring Boot, JPA/Hibernate
- **Banco de Dados**: PostgreSQL
- **Interface de Terminal**: JLine
- **Integração de API**: Consome dados da [Gutendex API](https://gutendex.com/) para dados de livros e autores.

---

## **Instruções de Configuração**

### Recomendação

- Java 17+
- PostgreSQL (Certifique-se de que o banco de dados esteja em execução e as credenciais estejam configuradas)
- Maven ou Gradle

### **Instalação**

1. Clone o repositório:
    
    `git clone https://github.com/fearauju/LiterAlura.git cd LiterAlura`

---

2. Configure seu arquivo `application.properties`:
    
    properties --> configure as variáveis de ambiente com seus respectivos dados
           
  `spring.application.name=LiterAlura`  
	`spring.datasource.url=jdbc:postgresql://${DB_HOST}/${DB_NAME}`  
	`spring.datasource.username=${DB_USER}`  
	`spring.datasource.password=${DB_PASSWORD}`  
	`spring.datasource.driver-class-name=org.postgresql.Driver`
	`hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect`  
	`spring.jpa.hibernate.ddl-auto=update`

---

## **Fluxo Funcional**

1. **Registro de Livros e Autores**:
    
    - Insira os detalhes do livro obtidos da Gutendex API ou adicione manualmente um título ou nome do autor.
    - Associe autores a livros.
2. **Explore Dados Através de Estatísticas**:
    
    - Escolha estatísticas do menu interativo para obter insights sobre os dados dos livros.
    - Exemplos:
        - Encontre os 10 livros mais baixados para cada idioma.
        - Determine o gênero predominante por idioma ou no geral.
3. **Realizar Operações de consulta**:
    
    - Use repositórios Spring Data JPA para consulta de dados flexíveis.

---

## **Destaques do Projeto**

- **Escalabilidade**: Projetado com serviços modulares para permitir extensões futuras.
- **Usabilidade**: Interface de terminal interativa com prompts amigáveis ao usuário.
- **Orientado a Insights**: Estatísticas para analisar dados de livros e autores.

---

## **Contribuindo**

Contribuições são bem-vindas! Faça um fork do repositório e crie um pull request para propor alterações ou novos recursos.

---

## **Autor**

Desenvolvido por [Luiz Felipe](https://github.com/fearauju).
