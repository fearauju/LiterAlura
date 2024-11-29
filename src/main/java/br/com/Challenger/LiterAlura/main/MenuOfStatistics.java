package br.com.Challenger.LiterAlura.main;

import br.com.Challenger.LiterAlura.service.BookService;
import br.com.Challenger.LiterAlura.service.BookWithAuthorProjection;
import br.com.Challenger.LiterAlura.service.TerminalColors;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MenuOfStatistics {

    private final BookService bookService;

    @Autowired
    public MenuOfStatistics(BookService bookService){
        this.bookService = bookService;
    }

    void displayMenu() {

        //criação do terminal
        try (Terminal terminal = TerminalBuilder.builder().system(true).build()) {

            LineReader reader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .completer(new StringsCompleter("1,2,3,4,5,6,7,8,9,0"))
                    .parser(new DefaultParser())
                    .build();


            terminal.writer().println(TerminalColors.colorize("\n---",TerminalColors.YELLOW) + TerminalColors.colorize(" Main Menu", TerminalColors.BOLD) + TerminalColors.colorize(" ---",TerminalColors.YELLOW));

            terminal.writer().println(TerminalColors.colorize("1. ", TerminalColors.BLUE) + TerminalColors.colorize(" Favorite books by language and specific statistics",TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("2. ", TerminalColors.BLUE) + TerminalColors.colorize(" Number of books by language and specific statistics",TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("3. ", TerminalColors.BLUE) + TerminalColors.colorize(" Public domain vs copyright books and specific statistics",TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("4. ", TerminalColors.BLUE) + TerminalColors.colorize(" Top books categorized by subject and other statistics", TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("5. ", TerminalColors.BLUE) + TerminalColors.colorize(" Average number of authors per book",TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("6. ", TerminalColors.BLUE) + TerminalColors.colorize(" Authors who published in more than one language",TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize("7. ", TerminalColors.BLUE) + TerminalColors.colorize(" Authors by genre",TerminalColors.CYAN));
            terminal.writer().println();

            String input = reader.readLine(TerminalColors.colorize("\n Choose an option: ",TerminalColors.BOLD)).trim();

            switch (input) {
                case "1":
                    favoriteBooksByLanguage(terminal);
                    break;
                case "2":
                    booksByLanguageStatistics(terminal);
                    break;
                case "3":
                    bookDomainStatistics(terminal);
                    break;
                case "4":
                    topBooksBySubject(terminal);
                    break;
                case "5":
                    averageAuthorsPerBook(terminal);
                    break;
                case "6":
                    authorsByLanguages(terminal);
                    break;
                case "7":
                    authorsByGenre(terminal);
                    break;
                default:
                    terminal.writer().println(TerminalColors.colorize("Invalid option! Please try again.",TerminalColors.RED));
            }
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(TerminalColors.colorize("Error processing terminal",TerminalColors.RED + e));
        }
    }

    private void favoriteBooksByLanguage(Terminal terminal) {

        var totalBooks = 0;
        List<String> genreList = new ArrayList<>();
        List<String> processedLanguages = new ArrayList<>();

        // Process books by language
        List<String> availableLanguages = bookService.findBooksByDistinctLanguage();

        for (String language : availableLanguages) {
            List<BookWithAuthorProjection> booksByLanguage = bookService.findTop10BooksByLanguage(language);

            for (BookWithAuthorProjection book : booksByLanguage) {

                System.out.println(TerminalColors.colorize("Title: ",TerminalColors.YELLOW) + TerminalColors.colorize(book.getBookTitle(),TerminalColors.BLUE));
                System.out.println(TerminalColors.colorize("Genre: ",TerminalColors.YELLOW) + TerminalColors.colorize(book.getGenre(),TerminalColors.BLUE));
                System.out.println(TerminalColors.colorize("Language: ",TerminalColors.YELLOW) + TerminalColors.colorize(book.getLanguages(),TerminalColors.BLUE));
                System.out.println(TerminalColors.colorize("Author: ",TerminalColors.YELLOW) + TerminalColors.colorize(book.getAuthorName(),TerminalColors.BLUE));
                System.out.println();

                genreList.add(book.getGenre());
                processedLanguages.add(book.getLanguages());
                totalBooks++;
            }
        }

        terminal.writer().println(TerminalColors.colorize(" ---",TerminalColors.YELLOW) + TerminalColors.colorize("Generic Statistics", TerminalColors.BOLD) + TerminalColors.colorize(" ---",TerminalColors.YELLOW));
        terminal.writer().println(TerminalColors.colorize("Total books: ",TerminalColors.CYAN) + TerminalColors.colorize(String.valueOf(totalBooks),TerminalColors.BLUE));

        // Find the predominant language
        String predominantLanguage = processedLanguages.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(TerminalColors.colorize("Not found",TerminalColors.CYAN));

        terminal.writer().println(TerminalColors.colorize("Predominant language: ",TerminalColors.CYAN) + TerminalColors.colorize(predominantLanguage,TerminalColors.BLUE));

        // Find the predominant genre
        String predominantGenre = genreList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(TerminalColors.colorize("Not found",TerminalColors.BLUE));

        terminal.writer().println(TerminalColors.colorize("Predominant genre: ",TerminalColors.CYAN) + TerminalColors.colorize(predominantGenre,TerminalColors.BLUE));

        // Calculate the percentage of the predominant genre
        long predominantGenreCount = genreList.stream()
                .filter(genre -> genre.equals(predominantGenre))
                .count();

        double genrePercentage = ((double) predominantGenreCount / totalBooks) * 100;
        terminal.writer().println(TerminalColors.colorize(String.format("The genre '%s' represents %.2f%% of the top books.", predominantGenre,genrePercentage), TerminalColors.YELLOW));
        terminal.flush();
    }


    private void booksByLanguageStatistics(Terminal terminal) {

        // Fetch languages and registered books
        List<String> languages = bookService.findBooksByDistinctLanguage();
        List<BookWithAuthorProjection> registeredBooks = bookService.findAllBooksWithAuthors();

        int totalBooks = registeredBooks.size();

        Map<String, Long> booksByLanguage = new HashMap<>();
        Map<String, String> predominantGenreByLanguage = new HashMap<>();

        terminal.writer().println(TerminalColors.colorize("\n---",TerminalColors.YELLOW) + TerminalColors.colorize(" Specific Statistics",TerminalColors.BOLD) + TerminalColors.colorize("---",TerminalColors.YELLOW));

        for (String language : languages) {

            // Fetch books for the current language
            List<BookWithAuthorProjection> booksByCurrentLanguage = bookService.findBooksByLanguages(Collections.singletonList(language));

            int totalBooksByLanguage = booksByCurrentLanguage.size();
            booksByLanguage.put(language, (long) totalBooksByLanguage);

            // Calculate the predominant genre for the current language
            String predominantGenre = getPredominantGenre(booksByCurrentLanguage);
            predominantGenreByLanguage.put(language, predominantGenre);

            // Calculate the percentage
            double percentage = (totalBooksByLanguage > 0)
                    ? ((double) totalBooksByLanguage / totalBooks) * 100
                    : 0;

            // Display specific statistics
            terminal.writer().println(TerminalColors.colorize(String.format("Language: %s",language),TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize(String.format("Total books in this language: %d, representing %.2f%% of the total.",totalBooksByLanguage,percentage),TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize(String.format("Predominant genre: %s", predominantGenre), TerminalColors.CYAN));
            terminal.writer().println();
            terminal.flush();
        }

        // General statistics
        terminal.writer().println(TerminalColors.colorize("\n ---",TerminalColors.YELLOW) + TerminalColors.colorize("Generic Statistics", TerminalColors.BOLD) + TerminalColors.colorize(" ---",TerminalColors.YELLOW));
        terminal.writer().println(TerminalColors.colorize("Total books processed: ",TerminalColors.YELLOW) + TerminalColors.colorize(String.valueOf(totalBooks),TerminalColors.BLUE));

        // Calculate the overall predominant genre
        String predominantGenre = getPredominantGenre(registeredBooks);

        long predominantGenreCount = registeredBooks.stream()
                .map(BookWithAuthorProjection::getGenre)
                .filter(Objects::nonNull)
                .filter(genre -> genre.equals(predominantGenre))
                .count();

        double genrePercentage = ((double) predominantGenreCount / totalBooks) * 100;

        terminal.writer().println(TerminalColors.colorize(String.format("\nThe genre '%s' represents %.2f%% of the total registered books.", predominantGenre, genrePercentage), TerminalColors.YELLOW));
        terminal.flush();
    }

    private void bookDomainStatistics(Terminal terminal) {
        List<BookWithAuthorProjection> allBooks = bookService.findAllBooksWithAuthors();
        int totalBooks = allBooks.size();

        DoubleSummaryStatistics percentStats = allBooks.stream()
                .collect(Collectors.summarizingDouble(book -> {
                    boolean isPublicDomain = !book.getCopyright();
                    return isPublicDomain ? 1.0 : 0.0;
                }));

        terminal.writer().println(TerminalColors.colorize("\n ---",TerminalColors.YELLOW) + TerminalColors.colorize("Generic Statistics", TerminalColors.BOLD) + TerminalColors.colorize(" ---",TerminalColors.YELLOW));

        terminal.writer().println(TerminalColors.colorize(String.format("Total books: %d", totalBooks), TerminalColors.CYAN));
        terminal.writer().println(TerminalColors.colorize(String.format("Public domain: %.2f%%", (percentStats.getSum() / totalBooks) * 100), TerminalColors.CYAN));
        terminal.writer().println(TerminalColors.colorize(String.format("With copyright: %.2f%%", 100 - (percentStats.getSum() / totalBooks) * 100), TerminalColors.CYAN));
        terminal.flush();
    }


    private void topBooksBySubject(Terminal terminal) {

        List<String> listSubjects = bookService.findBooksByDistinctSubjects();
        terminal.writer().println(TerminalColors.colorize("\n---",TerminalColors.YELLOW) + TerminalColors.colorize(" Specific Statistics",TerminalColors.BOLD) + TerminalColors.colorize("---",TerminalColors.YELLOW));

        for (String subject : listSubjects) {
            List<BookWithAuthorProjection> booksBySubject = bookService.searchBooksBySubjects(List.of(subject));

            int totalBooksBySubject = booksBySubject.size();
            int totalBooks = bookService.findAllBooksWithAuthors().size();

            double percentageBySubject = ((double) totalBooksBySubject / totalBooks) * 100;
            terminal.writer().println(TerminalColors.colorize(String.format("Total books in subject '%s': %d (%.2f%% of total)", subject, totalBooksBySubject, percentageBySubject), TerminalColors.CYAN));

            booksBySubject.stream()
                    .limit(10)
                    .forEach(book -> terminal.writer().println(TerminalColors.colorize(String.format("- %s (Author: %s, Language: %s)", book.getBookTitle(), book.getAuthorName(), book.getLanguages()), TerminalColors.BLUE)));
            terminal.writer().println();
            terminal.flush();
        }
    }

    private void averageAuthorsPerBook(Terminal terminal) {

        List<BookWithAuthorProjection> allBooks = bookService.findAllBooksWithAuthors();

        IntSummaryStatistics authorStats = allBooks.stream()
                .mapToInt(book -> book.getAuthorName().split(", ").length) // Counts the number of authors
                .summaryStatistics();

        terminal.writer().println(TerminalColors.colorize("\n---",TerminalColors.YELLOW) + TerminalColors.colorize(" Specific Statistics",TerminalColors.BOLD) + TerminalColors.colorize("---",TerminalColors.YELLOW));

        terminal.writer().println(TerminalColors.colorize(String.format("Total books: %d", authorStats.getCount()), TerminalColors.CYAN));
        terminal.writer().println(TerminalColors.colorize(String.format("Average authors per book: %.2f", authorStats.getAverage()), TerminalColors.CYAN));
        terminal.writer().println(TerminalColors.colorize(String.format("Minimum authors: %d", authorStats.getMin()), TerminalColors.CYAN));
        terminal.writer().println(TerminalColors.colorize(String.format("Maximum authors: %d", authorStats.getMax()), TerminalColors.CYAN));
        terminal.flush();
    }

    private void authorsByLanguages(Terminal terminal) {

        List<BookWithAuthorProjection> allBooks = bookService.findAllBooksWithAuthors();

        Map<String, Set<String>> authorLanguages = allBooks.stream()
                .collect(Collectors.groupingBy(
                        BookWithAuthorProjection::getAuthorName,
                        Collectors.mapping(BookWithAuthorProjection::getLanguages, Collectors.toSet())
                ));

        List<String> multilingualAuthors = authorLanguages.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .toList();

        terminal.writer().println(TerminalColors.colorize("\n---",TerminalColors.YELLOW) + TerminalColors.colorize("Authors Who Published in More Than One Language",TerminalColors.BOLD) + TerminalColors.colorize("---",TerminalColors.YELLOW));
        multilingualAuthors.forEach(author -> terminal.writer().println(TerminalColors.colorize(String.format("%s: %s", author, String.join(", ", authorLanguages.get(author))), TerminalColors.CYAN)));
        terminal.flush();
    }

    private void authorsByGenre(Terminal terminal) {

        List<BookWithAuthorProjection> allBooks = bookService.findAllBooksWithAuthors();

        Map<String, Set<String>> authorsByGenre = allBooks.stream()
                .collect(Collectors.groupingBy(
                        BookWithAuthorProjection::getGenre,
                        Collectors.mapping(BookWithAuthorProjection::getAuthorName, Collectors.toSet())
                ));

        terminal.writer().println(TerminalColors.colorize("\n---",TerminalColors.YELLOW) + TerminalColors.colorize("Author Statistics by Genre",TerminalColors.BOLD) + TerminalColors.colorize("---",TerminalColors.YELLOW));

        authorsByGenre.forEach((genre, authors) -> {
            terminal.writer().println(TerminalColors.colorize(String.format("Genre: %s", genre), TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize(String.format("Number of authors: %d", authors.size()),TerminalColors.CYAN));
            terminal.writer().println(TerminalColors.colorize(String.format("Authors: %s", String.join(", ", authors)),TerminalColors.CYAN));
        });
        terminal.flush();
    }

    private String getPredominantGenre(List<BookWithAuthorProjection> books) {

        return books.stream()
                .map(BookWithAuthorProjection::getGenre)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(TerminalColors.colorize("Not found",TerminalColors.CYAN));
    }
}
