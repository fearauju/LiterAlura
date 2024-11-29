package br.com.Challenger.LiterAlura.service;

import br.com.Challenger.LiterAlura.model.*;
import br.com.Challenger.LiterAlura.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookService {

    private final ConvertData convertData = new ConvertData();

    @Autowired
    private BookRepository bookRepository;


    @Autowired
    private PeopleService peopleService;

    @Transactional
    public void processBooksFromApi(String jsonResponse) throws IOException {

        // Converte os detalhes do livro
        List<BookDetails> bookDetails = convertData.fetchList(jsonResponse, BookDetails.class);
        List<Book> books = bookDetails.stream()
                .filter(b -> b.authorsDetails() != null && !b.authorsDetails().isEmpty())
                .map(b -> new Book(b.title(), b.languages(),b.subjects() ,b.direitosAutorais(), b.download_count()))
                .collect(Collectors.toList());

        // Converte os detalhes do autor
        List<PeopleDetails> peopleDetails = convertData.fetchList(jsonResponse, PeopleDetails.class);
        List<People> authors = peopleDetails.stream()
                .filter(b -> b.author() != null && !b.author().isEmpty())
                .map(a -> new People(a.author().getFirst())) // Mapeia do formato API para entidade
                .toList();

        System.out.println();


        // Normaliza e salva os autores usando o PeopleService
        String normalizedName = peopleService.normalizeName(authors.get(0).getName());
        People primaryAuthor = authors.get(0);
        primaryAuthor.setName(normalizedName);

        People managedAuthor = peopleService.saveOrUpdateAuthor(primaryAuthor);

        // Associa os livros ao autor normalizado
        saveBooksWithAuthor(managedAuthor, books);
    }

    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Transactional // Garantir que o método esteja dentro de uma transação ativa até que todas as operações sejam concluídas.
    public void processBookByTitle(String title) {

        List<Book> books = bookRepository.findByTitleContaining(title);

        if (!books.isEmpty()) {
            for (Book book : books) {
                System.out.println(TerminalColors.colorize(String.valueOf(book),TerminalColors.CYAN));
                List<People> authors = book.getAuthors();
                for (People author : authors) {
                    System.out.println(TerminalColors.colorize(String.valueOf(author),TerminalColors.YELLOW));
                    System.out.println();
                }
            }
        } else {
            System.out.println(TerminalColors.colorize("Book not found",TerminalColors.YELLOW));
            System.out.println();
        }
    }

    @Transactional
    public void saveBooksWithAuthor(People author, List<Book> books) {

        for (Book book : books) {
            Book existingBook = findByTitle(book.getTitle()).orElse(null);

            if (existingBook == null) {
                book.addAuthor(author);
                saveBook(book);
            } else {
                if (!existingBook.getAuthors().contains(author)) {
                    existingBook.addAuthor(author);
                }
            }
        }
    }

    public List<BookWithAuthorProjection> findAllBooksWithAuthors() {
        return bookRepository.findAllBooksWithAuthors();
    }

    public List<BookWithAuthorProjection> findBooksByLanguages(List<String> idioma) {
        return bookRepository.findBooksByLanguages(idioma);
    }

    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<BookWithAuthorProjection> searchBooksBySubjects(List<String> subjects) {
        String subjectsBook = String.join(",", subjects);
        return bookRepository.findBooksBySubjects(subjectsBook);
    }


    public List<BookWithAuthorProjection> findBookThisAuthor(String nameAuthor) {
        return bookRepository.findBookThisAuthor(nameAuthor);
    }

    public List<BookWithAuthorProjection> findBooksByCopyrightStatus(boolean copyright) {
       return bookRepository.findBooksByCopyrightStatus(copyright);
    }

    public List<String> findBooksByDistinctLanguage() {
        return bookRepository.findBooksByDistinctLanguage();
    }

    public List<BookWithAuthorProjection> findTop10BooksByLanguage(String language) {
        return bookRepository.findTop10BooksByLanguage(language);
    }

    public Integer findDistinctGenreCount() {
        return bookRepository.findDistinctGenreCount(Genre.UNDEFINED);
    }

    public List<String> findBooksByDistinctSubjects() {
        return bookRepository.findBooksByDistinctSubjects();
    }
}