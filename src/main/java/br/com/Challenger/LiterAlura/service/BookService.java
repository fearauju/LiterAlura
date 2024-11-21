package br.com.Challenger.LiterAlura.service;

import br.com.Challenger.LiterAlura.dto.BookWithAuthorDTO;
import br.com.Challenger.LiterAlura.model.Book;
import br.com.Challenger.LiterAlura.model.BookDetails;
import br.com.Challenger.LiterAlura.model.People;
import br.com.Challenger.LiterAlura.model.PeopleDetails;
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
                .map(b -> new Book(b.title(), b.languages(),b.subjects() ,b.direitosAutorais(), b.download_count()))
                .collect(Collectors.toList());

        // Converte os detalhes do autor
        List<PeopleDetails> peopleDetails = convertData.fetchList(jsonResponse, PeopleDetails.class);
        List<People> authors = peopleDetails.stream()
                .map(a -> new People(a.author().getFirst())) // Mapeia do formato API para entidade
                .collect(Collectors.toList());

        // Normaliza e salva os autores usando o PeopleService
        String normalizedName = peopleService.normalizeName(authors.get(0).getName());
        People primaryAuthor = authors.get(0);
        primaryAuthor.setName(normalizedName);

        People managedAuthor = peopleService.saveAuthor(primaryAuthor);

        // Associa os livros ao autor normalizado
        saveBooksWithAuthor(managedAuthor, books);
    }


    @Transactional
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    public void processBookByTitle(String title) {
        List<Book> books = bookRepository.findByTitleContaining(title);

        if (!books.isEmpty()) {
            for (Book book : books) {
                System.out.println(book);
                List<People> authors = book.getAuthors();
                for (People author : authors) {
                    System.out.println(author);
                    System.out.println();
                }
            }
        } else {
            System.out.println("Book not found");
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

    public List<Book> getAllBooksWithAuthors() {
        return bookRepository.findAllBooksWithAuthors();
    }

    public List<BookWithAuthorProjection> getBooksFilteredAndSorted(List<String> idioma) {
        return bookRepository.findBooksByLanguageAndSorted(idioma);
    }

    public Optional<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

    public List<Book> findBookByPublicDomain() {
        return bookRepository.findBookByPublicDomain();
    }

    public List<Book> findBookByCopyright() {
        return bookRepository.findBookByCopyright();
    }

    public List<BookWithAuthorProjection> searchBooksBySubjects(List<String> subjects) {
        String subjectsCsv = String.join(",", subjects);
        return bookRepository.findBooksBySubjects(subjectsCsv);
    }


    public List<Book> findBookThisAuthor(String nameAuthor) {
        return bookRepository.findBookThisAuthor(nameAuthor);
    }
}