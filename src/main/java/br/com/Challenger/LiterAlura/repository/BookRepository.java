package br.com.Challenger.LiterAlura.repository;

import br.com.Challenger.LiterAlura.dto.BookWithAuthorDTO;
import br.com.Challenger.LiterAlura.model.Book;
import br.com.Challenger.LiterAlura.service.BookWithAuthorProjection;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

 Optional<Book> findByTitle(String title);

 @Query("SELECT b FROM Book b JOIN b.authors a WHERE b.title ILIKE :title")
List<Book>findByTitleContaining(String title);

 @Query("SELECT DISTINCT b FROM Book b JOIN FETCH b.authors")
 List<Book> findAllBooksWithAuthors();



 @Query(value = """
    SELECT b.title AS bookTitle,
           STRING_AGG(DISTINCT lang.languages, ', ') AS languages,
           STRING_AGG(DISTINCT subj.subjects, ', ') AS subjects,
           b.copyright AS copyright,
           a.name AS authorName,
           a.birth_year AS birthYear,
           a.death_year AS deathYear
    FROM books b
    JOIN book_languages lang ON b.id = lang.book_id
    JOIN book_subjects subj ON b.id = subj.book_id
    JOIN book_people bp ON b.id = bp.book_id
    JOIN author a ON bp.people_id = a.id
    WHERE lang.languages IN :languages
    GROUP BY b.title, b.copyright, a.name, a.birth_year, a.death_year
    ORDER BY b.title ASC
""", nativeQuery = true)
 List<BookWithAuthorProjection> findBooksByLanguageAndSorted(@Param("languages") List<String> languages);

 @Query("SELECT b FROM Book b JOIN FETCH b.authors a WHERE b.copyright IS false")
 List<Book>findBookByPublicDomain();

 @Query("SELECT b FROM Book b JOIN FETCH b.authors a WHERE b.copyright IS true")
 List<Book>findBookByCopyright();

 @Query(value = """
    SELECT b.title AS bookTitle,
           STRING_AGG(DISTINCT lang.languages, ', ') AS languages,
           STRING_AGG(DISTINCT subj.subjects, ', ') AS subjects,
           b.copyright AS copyright,
           a.name AS authorName,
           a.birth_year AS birthYear,
           a.death_year AS deathYear
    FROM books b
    JOIN book_languages lang ON b.id = lang.book_id
    JOIN book_subjects subj ON b.id = subj.book_id
    JOIN book_people bp ON b.id = bp.book_id
    JOIN author a ON bp.people_id = a.id
    WHERE 
        EXISTS (
            SELECT 1
            FROM UNNEST(STRING_TO_ARRAY(:subjects, ',')) AS search_subject
            WHERE LOWER(subj.subjects) ILIKE '%' || search_subject || '%'
        )
    GROUP BY b.title, b.copyright, a.name, a.birth_year, a.death_year
    ORDER BY b.title ASC
""", nativeQuery = true)
 List<BookWithAuthorProjection> findBooksBySubjects(@Param("subjects") String subjects);


 @Query("SELECT b FROM Book b JOIN FETCH b.authors a WHERE a.name = %:nameAuthor%")
 List<Book> findBookThisAuthor(String nameAuthor);
}


