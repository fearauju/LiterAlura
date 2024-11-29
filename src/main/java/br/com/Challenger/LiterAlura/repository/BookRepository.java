package br.com.Challenger.LiterAlura.repository;

import br.com.Challenger.LiterAlura.model.Book;
import br.com.Challenger.LiterAlura.model.Genre;
import br.com.Challenger.LiterAlura.service.BookWithAuthorProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

 Optional<Book> findByTitle(String title);

 @Query("SELECT b FROM Book b JOIN FETCH b.authors a WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
 List<Book> findByTitleContaining(@Param("title") String title);

 @Query(value = """
    SELECT b.title AS bookTitle,
           STRING_AGG(DISTINCT lang.languages, ', ') AS languages,
           STRING_AGG(DISTINCT subj.subjects, ', ') AS subjects,
           b.copyright AS copyright,
           b.download_count AS downloadCount,
           b.genre As genre,
           a.name AS authorName
    FROM books b
    JOIN book_languages lang ON b.id = lang.book_id
    JOIN book_subjects subj ON b.id = subj.book_id
    JOIN book_people bp ON b.id = bp.book_id
    JOIN author a ON bp.people_id = a.id
    GROUP BY b.title, b.copyright, b.download_count, b.genre, a.name
    Order by a.name ASC
""", nativeQuery = true)
 List<BookWithAuthorProjection> findAllBooksWithAuthors();

 @Query(value = """
    SELECT b.title AS bookTitle,
           STRING_AGG(DISTINCT lang.languages, ', ') AS languages,
           STRING_AGG(DISTINCT subj.subjects, ', ') AS subjects,
           b.copyright AS copyright,
           b.genre AS genre,
           a.name AS authorName
    FROM books b
    JOIN book_languages lang ON b.id = lang.book_id
    JOIN book_subjects subj ON b.id = subj.book_id
    JOIN book_people bp ON b.id = bp.book_id
    JOIN author a ON bp.people_id = a.id
    WHERE lang.languages IN :languages
    GROUP BY b.title, b.copyright, b.genre, a.name
    ORDER BY b.title ASC
""", nativeQuery = true)
 List<BookWithAuthorProjection> findBooksByLanguages(@Param("languages") List<String> languages);

 @Query(value = """
    SELECT b.title AS bookTitle,
           STRING_AGG(DISTINCT lang.languages, ', ') AS languages,
           STRING_AGG(DISTINCT subj.subjects, ', ') AS subjects,
           b.copyright AS copyright,
           b.download_count As downloadCount,
           a.name AS authorName
    FROM books b
    JOIN book_languages lang ON b.id = lang.book_id
    JOIN book_subjects subj ON b.id = subj.book_id
    JOIN book_people bp ON b.id = bp.book_id
    JOIN author a ON bp.people_id = a.id
    WHERE b.copyright =:isCopyright
    GROUP BY b.title, b.copyright, b.download_count, a.name
    ORDER BY b.title ASC
""", nativeQuery = true)
 List<BookWithAuthorProjection> findBooksByCopyrightStatus(@Param("isCopyright") boolean isCopyright);

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

 @Query(value = """
    SELECT b.title AS bookTitle,
           STRING_AGG(DISTINCT lang.languages, ', ') AS languages,
           STRING_AGG(DISTINCT subj.subjects, ', ') AS subjects,
           b.copyright AS copyright,
           b.download_count AS downloadCount,
           a.name AS authorName,
           a.birth_year AS birthYear,
           a.death_year AS deathYear
    FROM books b
    JOIN book_languages lang ON b.id = lang.book_id
    JOIN book_subjects subj ON b.id = subj.book_id
    JOIN book_people bp ON b.id = bp.book_id
    JOIN author a ON bp.people_id = a.id
    WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :nameAuthor, '%'))
    GROUP BY b.title, b.copyright, b.download_count, a.name, a.birth_year, a.death_year
""", nativeQuery = true)
 List<BookWithAuthorProjection> findBookThisAuthor(@Param("nameAuthor") String nameAuthor);

 @Query("SELECT Distinct languages FROM Book")
 List<String> findBooksByDistinctLanguage();

 @Query(value = """
    SELECT b.title AS bookTitle,
           STRING_AGG(DISTINCT lang.languages, ', ') AS languages,
           STRING_AGG(DISTINCT subj.subjects, ', ') AS subjects,
           b.download_count AS downloadCount,
           b.genre AS genre,
           a.name AS authorName
    FROM books b
    JOIN book_languages lang ON b.id = lang.book_id
    JOIN book_subjects subj ON b.id = subj.book_id
    JOIN book_people bp ON b.id = bp.book_id
    JOIN author a ON bp.people_id = a.id
    WHERE lang.languages = :language
    GROUP BY b.title, b.download_count, b.genre, a.name
    ORDER BY b.download_count DESC
    LIMIT 10
""", nativeQuery = true)
 List<BookWithAuthorProjection> findTop10BooksByLanguage(@Param("language") String language);


 @Query("SELECT COUNT(DISTINCT b.genre) FROM Book b WHERE b.genre <> :undefined")
 Integer findDistinctGenreCount(@Param("undefined") Genre undefined);

 @Query("SELECT Distinct subjects FROM Book")
 List<String> findBooksByDistinctSubjects();


}