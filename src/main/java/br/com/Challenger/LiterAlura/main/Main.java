package br.com.Challenger.LiterAlura.main;

import br.com.Challenger.LiterAlura.dto.AuthorDTO;
import br.com.Challenger.LiterAlura.model.Book;
import br.com.Challenger.LiterAlura.model.People;
import br.com.Challenger.LiterAlura.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


@Component()
public class Main {

    private int option;
    private final Scanner scanner = new Scanner(System.in);
    private boolean isRunning = true;
    private final ApiConsumption apiConsumption = new ApiConsumption();


    @Autowired
    private PeopleService authorService;

    @Autowired
    private BookService bookService;

    public Main(PeopleService authorService, BookService bookService) {
        this.authorService = authorService;
        this.bookService = bookService;
    }

    public void displayMenu() {

        String menu = """
            1 - List books by title
            2 - List books by an Author
            3 - List registered books
            4 - List registered author
            5 - List books by language
            6 - List historical author
            7 - List books by public domain
            8 - List books with copyright
            9 - List books by topic
            
            0 - Exit
            """;

        while (isRunning) {

            System.out.println(menu);

            if( scanner.hasNextInt()){
                option = scanner.nextInt();
                scanner.nextLine();
                displayMenuOptions();
            }else {
                System.out.println("Please enter one of the numeric option");
                scanner.nextLine();
                System.out.println();
            }
        }
    }

    private void displayMenuOptions()  {
        switch (option) {
            case 1 -> listBookByTitle();
            case 2 -> listBooksByAnAuthor();
            case 3 -> listRegisteredBook();
            case 4 -> listRegisteredAuthors();
            case 5 -> listBooksByLanguage();
            case 6 -> listHistoricalAuthors();
            case 7 -> ListarLivrosDeDominioPublico();
            case 8 -> listarLivrosComDireitosAutorais();
            case 9 -> findBookBySubjects();
            case 0 -> {
                System.out.println();
                System.out.println("Exiting the program");
                isRunning = false;
            }
            default -> System.out.println("Select one of the available options!");
        }
    }

    private void listBooksByAnAuthor() {
        System.out.print("Enter name of author: ");
        String inputName = scanner.nextLine();

        // Normaliza o nome antes da busca
        String normalizedInputName = authorService.normalizeName(inputName);

        List<People> authors = authorService.findByNameAuthor(normalizedInputName);

        if (!authors.isEmpty()) {
            authors.forEach(a ->
                    System.out.println("Name: " + a.getName() + "\n" +
                            "Birth year: " + a.getBirthYear() + "\n" +
                            "Death year: " + a.getDeathYear()));
            System.out.println();

            for (People author : authors){
                List<Book> booksAuthor = bookService.findBookThisAuthor(author.getName());
                booksAuthor.forEach(System.out::println);
            }
        } else {
            System.out.println("Author not found.");
        }
    }

    private void listBookByTitle() {

        System.out.println("Digite o título do livro: ");
        var tituloLivro = scanner.nextLine();


        String url = "https://gutendex.com/books/?&search=" + tituloLivro.replace(" ", "%20");

        String json = apiConsumption.fetchData(url);

        try {
            if (JsonChecker.isJsonEmpty(json)) {
                System.out.println("Esses são todos os dados que temos para essa pesquisa");
                System.out.println();

                return;
            } else {

                bookService.processBooksFromApi(json);
                bookService.processBookByTitle(tituloLivro);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        bookService.processBookByTitle(tituloLivro);
    }

    private void listBooksByLanguage() {

        System.out.println("Idiomas disponíveis: ");
        System.out.println();

        String menuIdiomas = """ 
               _______________________
                1 - pt --> Português
                2 - es --> Espanhol
                3 - en --> English
                4 - fr --> Francês
                5 - fl --> Finlandes
                6 - pl --> Polonês
                7 - it --> Italiano
                ______________________
               """;

        System.out.println(menuIdiomas);
        System.out.print("Instruções de consulta: ");
        System.out.println();

        String opcoesConsulta = """
                ---------------------
                 Formas de realizar a consulta:
                
                 Pesquisar por livros por um único idioma --> Ex: pt
                 Pesquisar por livros por mais de um idioma --> Ex: pt,es,fr
                ---------------------
                """;

        System.out.println(opcoesConsulta);
        System.out.println();

        System.out.print("Idioma: ");
        String inputIdiomas = scanner.nextLine().toLowerCase().trim();
        List<String> idiomas = Arrays.asList(inputIdiomas.split(","));
        List<BookWithAuthorProjection> booksWithAuthors = bookService.getBooksFilteredAndSorted(idiomas);

        for(BookWithAuthorProjection book : booksWithAuthors) {

            System.out.println("------------------------------------");
            System.out.println("Book Title: " + book.getBookTitle());
            System.out.println("Language: " + book.getLanguages());
            System.out.println("Subjects: " + book.getSubjects());
            System.out.println("Copyright: " + (book.getCopyright() ? "Has Copyright" : "Public Domain"));
            System.out.println("Author: " + book.getAuthorName());
            System.out.println("Birth Year: " + book.getBirthYear());
            System.out.println("Death Year: " + book.getDeathYear());
            System.out.println("------------------------------------");
            System.out.println();
        }
    }

    private void listHistoricalAuthors() {

        boolean isYear = false;
        var year = 0;

        while(!isYear){
            System.out.print("Digite o ano que deseja pesquisar: ");

            if(scanner.hasNextInt()){
                year = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                isYear = true;
            }else{
                scanner.nextLine();
                System.out.println("Por favor entre com valor numérico inteiro");
                System.out.println();
            }
        }

        List<AuthorDTO> authorSInPeriod = authorService.findAuthorsByCentury(year);
        authorSInPeriod
                .forEach(a ->
                        System.out.println("\nName: " + a.authorName() + " Birth Year: " + a.birthYear() + " Death Year: " + a.deathYear()));
        System.out.println();
    }

    private void listRegisteredAuthors() {

        List<People> authors = authorService.findAllAuthors();

        for(People author : authors) {

            String authorName = author.getName();
            Integer birthYear = author.getBirthYear();
            Integer deathYear = author.getDeathYear();

            System.out.println("Name: " + authorName);
            System.out.println("Birth Year: " + birthYear);
            System.out.println("Death Year: " + deathYear);
            System.out.println();
        }
    }

    @Transactional
    private void listRegisteredBook() {

        List<Book> registeredBooks = bookService.getAllBooksWithAuthors();

        for (Book book : registeredBooks) {

            String bookTitle = book.getTitle();
            String name = book.getAuthors().getFirst().getName();
            int birthYear = book.getAuthors().getFirst().getBirthYear() == null ? 0 : book.getAuthors().getFirst().getBirthYear();
            int deathYear = book.getAuthors().getFirst().getDeathYear() == null ? 0 : book.getAuthors().getFirst().getDeathYear();

            System.out.println("Título: " + bookTitle);
            System.out.println("Language: " + book.getLanguages());
            System.out.println(book.getCopyright());
            System.out.println("Autor: " + name);
            System.out.println("Birth Year: " + birthYear);
            System.out.println("Death Year: " + deathYear);
            System.out.println();
        }
    }

    private void ListarLivrosDeDominioPublico() {

        List<Book>publicDomainBooks = bookService.findBookByPublicDomain();
        publicDomainBooks.forEach(System.out::println);

    }

    private void listarLivrosComDireitosAutorais() {

        List<Book>protectedBooks = bookService.findBookByCopyright();
        protectedBooks.forEach(System.out::println);
    }

    private void findBookBySubjects() {

        String explicacao = """
                Você pode pesquisar livros sobre determinados assuntos.
                Também funciona caso queira pesquisar por um gênero específico.
                Ex: fiction, drama, poetry, etc.
                """;
        System.out.println(explicacao);
        System.out.println();

        System.out.println("Digite o assunto ou gênero que deseja pesquisar: ");
        var subjectQuery = scanner.nextLine().toLowerCase().trim();

        List<String> subjects = Arrays.asList(subjectQuery.toLowerCase().split(","));
        List<BookWithAuthorProjection> booksWithAuthors = bookService.searchBooksBySubjects(subjects);

        for (BookWithAuthorProjection book : booksWithAuthors) {

            System.out.println("------------------------------------");
            System.out.println("Title: " + book.getBookTitle());
            System.out.println("Languages: " + book.getLanguages());
            System.out.println("Subjects: " + book.getSubjects());
            System.out.println("Author: " + book.getAuthorName());
            System.out.println("Birth Year: " + book.getBirthYear());
            System.out.println("Death Year: " + book.getDeathYear());
            System.out.println("------------------------------------");
            System.out.println();
        }
    }
}

