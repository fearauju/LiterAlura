package br.com.Challenger.LiterAlura.main;

import br.com.Challenger.LiterAlura.dto.AuthorDTO;
import br.com.Challenger.LiterAlura.exception.DataProcessingException;
import br.com.Challenger.LiterAlura.model.People;
import br.com.Challenger.LiterAlura.service.*;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Component
public class MenuHandler {

    private final Map<Integer, Runnable> menuActions = new HashMap<>();
    private final BookService bookService;
    private final PeopleService peopleService;
    private final Scanner scanner;
    private final ApiConsumption apiConsumption;
    private final MenuOfStatistics menuOfStatistics;

    @Autowired
    public MenuHandler(BookService bookService, PeopleService peopleService, MenuOfStatistics menuOfStatistics) {
        this.bookService = bookService;
        this.peopleService = peopleService;
        this.scanner = new Scanner(System.in);
        this.apiConsumption = new ApiConsumption();
        this.menuOfStatistics = menuOfStatistics;

        // Mapear ações do menu para métodos
        menuActions.put(1, this::listBooksByTitle);
        menuActions.put(2, this::listBooksByAnAuthor);
        menuActions.put(3, this::listRegisteredBooks);
        menuActions.put(4, this::listRegisteredAuthors);
        menuActions.put(5, this::listBooksByLanguage);
        menuActions.put(6, this::listHistoricalAuthors);
        menuActions.put(7, this::listPublicDomainBooks);
        menuActions.put(8, this::listCopyrightBooks);
        menuActions.put(9, this::findBooksBySubject);
        menuActions.put(10,this::showMenuStatistics);
    }


    // Exibir menu
    public void showMenu() {

        //criação do terminal
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new StringsCompleter("1,2,3,4,5,6,7,8,9,10,0"))
                    .parser(new DefaultParser())
                    .build();

            terminal.writer().println(TerminalColors.colorize("\n---", TerminalColors.YELLOW) + TerminalColors.colorize(" Main Menu", TerminalColors.BOLD) + TerminalColors.colorize(" ---", TerminalColors.YELLOW));

            terminal.writer().println(TerminalColors.colorize("1. ", TerminalColors.BLUE) + TerminalColors.colorize(" List books by title", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("2. ", TerminalColors.BLUE) + TerminalColors.colorize(" List books by an Author", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("3. ", TerminalColors.BLUE) + TerminalColors.colorize(" List registered books", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("4. ", TerminalColors.BLUE) + TerminalColors.colorize(" List registered authors", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("5. ", TerminalColors.BLUE) + TerminalColors.colorize(" List books by language", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("6. ", TerminalColors.BLUE) + TerminalColors.colorize(" List historical authors", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("7. ", TerminalColors.BLUE) + TerminalColors.colorize(" List books in public domain", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("8. ", TerminalColors.BLUE) + TerminalColors.colorize(" List books with copyright", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("9. ", TerminalColors.BLUE) + TerminalColors.colorize(" List books by subject", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("10. ", TerminalColors.BLUE) + TerminalColors.colorize(" Statistics", TerminalColors.CYAN));
            terminal.writer().println();
            terminal.writer().println(TerminalColors.colorize("0 ", TerminalColors.BLUE) + TerminalColors.colorize("Exit", TerminalColors.CYAN));
            terminal.writer().println();
            terminal.flush();

        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(TerminalColors.colorize("Error processing terminal",TerminalColors.RED + e));
        }
    }

        // Lê a opção selecionada pelo utilizador
    public int readOption(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println(TerminalColors.colorize("Please enter a valid numeric option!",TerminalColors.RED));
            scanner.nextLine();
        }
        int option = scanner.nextInt();
        scanner.nextLine(); // Consumir a quebra de linha
        return option;
    }

    // Executar ação correspondente à opção do menu
    public void executeOption(int option) {
        Runnable action = menuActions.get(option);
        if (action != null) {
            action.run(); // Executa a ação correspondente
        } else {
            System.out.println(TerminalColors.colorize("Invalid option! Please try again.", TerminalColors.RED));
        }
    }


    // Métodos específicos de ação (delegam para serviços)

    private void listBooksByTitle() {

        System.out.print(TerminalColors.colorize("Enter the book title: ",TerminalColors.CYAN));
        var bookTitle = scanner.nextLine().trim();

        String url = "https://gutendex.com/books/?&search=" + bookTitle.replace(" ", "%20");

        String json = apiConsumption.fetchData(url);

        try {
            if (ConvertData.isJsonEmpty(json)) {
                System.out.println(TerminalColors.colorize("These are all the results we have for this search",TerminalColors.YELLOW));
                System.out.println();
            } else {

                bookService.processBooksFromApi(json);
                bookService.processBookByTitle(bookTitle);
            }
        } catch (DataProcessingException | IOException e) {
            throw new DataProcessingException(TerminalColors.colorize("An error occurred while processing data from the API.",TerminalColors.RED), e);
        }
    }

    @Transactional
    public void listBooksByAnAuthor() {

        System.out.print(TerminalColors.colorize("Enter name of author: ",TerminalColors.CYAN));
        String inputName = scanner.nextLine();

        // Normaliza o nome antes da busca
        String normalizedInputName = peopleService.normalizeName(inputName);

        List<People> authors = peopleService.findByNameContaining(normalizedInputName);

        if (!authors.isEmpty()) {
            authors.forEach(a ->
                    System.out.println(TerminalColors.colorize("Name: ",TerminalColors.YELLOW) + TerminalColors.colorize(a.getName(),TerminalColors.BLUE) + "\n" +
                            TerminalColors.colorize("Birth year: ",TerminalColors.YELLOW) + TerminalColors.colorize(String.valueOf(a.getBirthYear()),TerminalColors.BLUE) + "\n" +
                            TerminalColors.colorize("Death year: ",TerminalColors.YELLOW) + TerminalColors.colorize(String.valueOf(a.getDeathYear()),TerminalColors.BLUE)));
            System.out.println();

            for (People author : authors) {
                List<BookWithAuthorProjection> booksAuthor = bookService.findBookThisAuthor(author.getName());
                booksAuthor.forEach(book -> {

                    System.out.println(TerminalColors.colorize("Author: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getAuthorName(),TerminalColors.CYAN));
                    System.out.println();
                    System.out.println(TerminalColors.colorize("Title: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getBookTitle(),TerminalColors.CYAN));
                    System.out.println(TerminalColors.colorize("Languages: ",TerminalColors.BLUE) + String.join(", ", TerminalColors.colorize(book.getLanguages(),TerminalColors.CYAN))); // Inicializa aqui, evita o problema de carregamento lento
                    System.out.println(TerminalColors.colorize("Subjects: ",TerminalColors.BLUE) + String.join(", ", TerminalColors.colorize(book.getSubjects(),TerminalColors.CYAN))); // Inicializa aqui, evita o problema de carregamento lento
                    System.out.println(TerminalColors.colorize("Copyright: ",TerminalColors.BLUE) + (book.getCopyright() ? TerminalColors.colorize("Has Copyright",TerminalColors.CYAN) :
                            TerminalColors.colorize("Public Domain",TerminalColors.CYAN)));
                    System.out.println(TerminalColors.colorize("Downloads: ",TerminalColors.BLUE) + TerminalColors.colorize(String.valueOf(book.getDownloadCount()),TerminalColors.CYAN));
                    System.out.println();
                });
            }
        } else {
            System.out.println(TerminalColors.colorize("Author not found.",TerminalColors.YELLOW));
        }
    }

    @Transactional
    private void listRegisteredBooks() {

        List<BookWithAuthorProjection> registeredBooks = bookService.findAllBooksWithAuthors();

        if(!registeredBooks.isEmpty()) {
            for (BookWithAuthorProjection book : registeredBooks) {

                System.out.println(TerminalColors.colorize("Author: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getAuthorName(),TerminalColors.CYAN));
                System.out.println();
                System.out.println(TerminalColors.colorize("Title: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getBookTitle(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Language: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getLanguages(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Subjects: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getSubjects(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Copyright: ",TerminalColors.BLUE) + (book.getCopyright() ? TerminalColors.colorize("Has copyright",TerminalColors.CYAN) :
                        TerminalColors.colorize("PublicDomain",TerminalColors.CYAN)));
                System.out.println();
            }
        }else {
            System.out.println(TerminalColors.colorize("Books not found.",TerminalColors.YELLOW));
        }
    }

    private void listRegisteredAuthors() {

        List<People> authors = peopleService.listAllAuthors();

        if(!authors.isEmpty()) {
            for (People author : authors) {

                String authorName = author.getName();
                Integer birthYear = author.getBirthYear();
                Integer deathYear = author.getDeathYear();

                System.out.println(TerminalColors.colorize("Name: ",TerminalColors.YELLOW) + TerminalColors.colorize(authorName,TerminalColors.BLUE));
                System.out.println(TerminalColors.colorize("Birth Year: ",TerminalColors.YELLOW) + TerminalColors.colorize(String.valueOf(birthYear),TerminalColors.BLUE));
                System.out.println(TerminalColors.colorize("Death Year: ",TerminalColors.YELLOW) + TerminalColors.colorize(String.valueOf(deathYear),TerminalColors.BLUE));
                System.out.println();
            }
        }else {
            System.out.println(TerminalColors.colorize("Author not found.",TerminalColors.YELLOW));
        }
    }

    public void listBooksByLanguage() {

        System.out.println(TerminalColors.colorize("Available languages: ",TerminalColors.BOLD));
        System.out.println();

        String menuLanguages = """ 
        _______________________
         1 - pt --> Portuguese
         2 - es --> Spanish
         3 - en --> English
         4 - fr --> French
         5 - fl --> Finnish
         6 - pl --> Polish
         7 - it --> Italian
         ______________________
        """;

        System.out.println(TerminalColors.colorize(menuLanguages,TerminalColors.BLUE));
        System.out.println();

        String queryInstructions = """
        ---------------------
        Query options:
        Search for books in a single language --> Ex: pt
        Search for books in multiple languages --> Ex: pt,es,fr
        ---------------------
        """;

        System.out.println(TerminalColors.colorize(queryInstructions,TerminalColors.GREEN));
        System.out.println();

        System.out.print(TerminalColors.colorize("Enter with Language(s): ",TerminalColors.CYAN));
        String inputLanguages = scanner.nextLine().toLowerCase().trim();
        List<String> languages = Arrays.asList(inputLanguages.split(","));
        List<BookWithAuthorProjection> booksWithAuthors = bookService.findBooksByLanguages(languages);

        if(!booksWithAuthors.isEmpty()) {

            for (BookWithAuthorProjection book : booksWithAuthors) {

                System.out.println(TerminalColors.colorize("Book Title: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getBookTitle(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Language: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getLanguages(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize( "Subjects: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getSubjects(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Copyright: ",TerminalColors.BLUE) + (book.getCopyright() ? TerminalColors.colorize("Has Copyright",TerminalColors.CYAN)
                        : TerminalColors.colorize("Public Domain",TerminalColors.CYAN)));
                System.out.println(TerminalColors.colorize("Author: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getAuthorName(),TerminalColors.CYAN));
                System.out.println();
            }
        }else {
            System.out.println(TerminalColors.colorize("Books not found.",TerminalColors.YELLOW));
        }
    }


    private void listHistoricalAuthors() {

        boolean isYear = false;
        var year = 0;

        while(!isYear){
            System.out.print(TerminalColors.colorize("Enter the year you want to search for: ",TerminalColors.CYAN));

            if(scanner.hasNextInt()){
                year = scanner.nextInt();
                scanner.nextLine();
                System.out.println();
                isYear = true;
            }else{
                scanner.nextLine();
                System.out.println(TerminalColors.colorize("Please enter a valid integer number.",TerminalColors.YELLOW));
                System.out.println();
            }
        }

        List<AuthorDTO> authorSInPeriod = peopleService.findAuthorsByCentury(year);

        if(!authorSInPeriod.isEmpty()) {
            authorSInPeriod
                    .forEach(a ->
                            System.out.println(TerminalColors.colorize( "\n Name: ",TerminalColors.BLUE) + TerminalColors.colorize(a.authorName(),TerminalColors.CYAN) +
                                    TerminalColors.colorize(" Birth Year: ",TerminalColors.BLUE) + TerminalColors.colorize(String.valueOf(a.birthYear()),TerminalColors.CYAN) +
                                    TerminalColors.colorize(" Death Year: ",TerminalColors.BLUE) + TerminalColors.colorize(String.valueOf(a.deathYear()),TerminalColors.CYAN)));
            System.out.println();
        }else {
            System.out.println(TerminalColors.colorize("No authors found for the specified year.",TerminalColors.YELLOW));
        }
    }

    @Transactional
    private void listPublicDomainBooks() {

        List<BookWithAuthorProjection> booksPublicDomain = bookService.findBooksByCopyrightStatus(false);

        if(!booksPublicDomain.isEmpty()) {
            System.out.println(TerminalColors.colorize( "Public Domain Books",TerminalColors.BOLD));
            System.out.println();
            booksPublicDomain.forEach(book -> {
                System.out.println();
                System.out.println(TerminalColors.colorize( "Author: ",TerminalColors.YELLOW) + TerminalColors.colorize(book.getAuthorName(),TerminalColors.BLUE));
                System.out.println();
                System.out.println(TerminalColors.colorize( "Title: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getBookTitle(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize( "Language: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getLanguages(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize( "Subjects: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getSubjects(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize( "Copyright: ",TerminalColors.BLUE) + (book.getCopyright() ? TerminalColors.colorize( "Has copyright",TerminalColors.CYAN)
                        : TerminalColors.colorize( "Public Domain",TerminalColors.CYAN)));
                System.out.println(TerminalColors.colorize( "Downloads: ",TerminalColors.BLUE) + TerminalColors.colorize(String.valueOf(book.getDownloadCount()),TerminalColors.CYAN));
                System.out.println();
            });
        }else {
            System.out.println(TerminalColors.colorize( "No public domain books are currently registered.",TerminalColors.YELLOW));
        }
    }

    @Transactional
    private void listCopyrightBooks() {

        List<BookWithAuthorProjection> booksCopyright = bookService.findBooksByCopyrightStatus(true);

        if(!booksCopyright.isEmpty()) {
            System.out.println(TerminalColors.colorize("Books with Copyright",TerminalColors.YELLOW));
            System.out.println();
            booksCopyright.forEach(book -> {
                System.out.println(TerminalColors.colorize("Author: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getAuthorName(),TerminalColors.CYAN));
                System.out.println();
                System.out.println(TerminalColors.colorize("Title: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getBookTitle(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Language: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getLanguages(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Subjects: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getSubjects(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Copyright: ",TerminalColors.BLUE) + (book.getCopyright() ? TerminalColors.colorize("Has copyright",
                        TerminalColors.CYAN) : TerminalColors.colorize("Public Domain",TerminalColors.CYAN)));
                System.out.println(TerminalColors.colorize("Downloads: ",TerminalColors.BLUE) + TerminalColors.colorize(String.valueOf(book.getDownloadCount()),TerminalColors.CYAN));
                System.out.println();
            });
        }else {
            System.out.println(TerminalColors.colorize("No books with copyright are currently registered.",TerminalColors.YELLOW));
        }
    }

    private void findBooksBySubject() {

        String explanation = """
                You can search for books on specific topics or genres.
                        For example: fiction, drama, poetry, etc.
                """;
        System.out.println(TerminalColors.colorize(explanation,TerminalColors.YELLOW));
        System.out.println();

        System.out.println(TerminalColors.colorize("Enter the subject or genre you want to search for: ",TerminalColors.BLUE));
        var subjectQuery = scanner.nextLine().toLowerCase().trim();

        List<String> subjects = Arrays.asList(subjectQuery.toLowerCase().split(","));

        List<BookWithAuthorProjection> booksWithAuthors = bookService.searchBooksBySubjects(subjects);

        if(!booksWithAuthors.isEmpty()) {
            for (BookWithAuthorProjection book : booksWithAuthors) {


                System.out.println(TerminalColors.colorize("Title: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getBookTitle(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Languages: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getLanguages(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Subjects: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getSubjects(),TerminalColors.CYAN));
                System.out.println(TerminalColors.colorize("Author: ",TerminalColors.BLUE) + TerminalColors.colorize(book.getAuthorName(),TerminalColors.CYAN) +
                        " " + TerminalColors.colorize(String.valueOf(book.getBirthYear()),TerminalColors.CYAN) + " "
                        + TerminalColors.colorize(String.valueOf(book.getDeathYear()),TerminalColors.CYAN));
                System.out.println();
            }
        }else {
            System.out.println(TerminalColors.colorize("No books found for the specified subject.",TerminalColors.YELLOW));
        }
    }

    public void showMenuStatistics( ) {
        boolean exit = false;

        while (!exit) {
            System.out.println(TerminalColors.colorize("\n---",TerminalColors.YELLOW) +
                    TerminalColors.colorize("Statistics Menu",TerminalColors.BOLD) +
                    TerminalColors.colorize(" ---",TerminalColors.YELLOW));
            System.out.println(TerminalColors.colorize("0 -",TerminalColors.BLUE) +
                    TerminalColors.colorize(" Exit",TerminalColors.CYAN));
            System.out.println(TerminalColors.colorize("1 -",TerminalColors.BLUE) +
                    TerminalColors.colorize(" Display Menu",TerminalColors.CYAN));
            System.out.println();

            System.out.print(TerminalColors.colorize("Enter a menu option: ",TerminalColors.CYAN));
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    menuOfStatistics.displayMenu(); // Chama o menu de estatísticas
                    break;
                case "0":
                    System.out.println(TerminalColors.colorize("Returning to the main menu...",TerminalColors.YELLOW));
                    exit = true;
                    break;
                default:
                    System.out.println(TerminalColors.colorize("Invalid option! Please try again.",TerminalColors.YELLOW));
            }
            System.out.println();
        }
    }
}

