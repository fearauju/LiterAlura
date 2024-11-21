package br.com.Challenger.LiterAlura.repository;

import br.com.Challenger.LiterAlura.dto.AuthorDTO;
import br.com.Challenger.LiterAlura.dto.BookWithAuthorDTO;
import br.com.Challenger.LiterAlura.model.People;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeopleRepository extends JpaRepository<People, Long> {

    boolean existsByName(String name);



    @Query("SELECT a FROM People a WHERE LOWER(a.name) = LOWER(:name)")
    List<People> findByNameAuthor(@Param("name") String name);


    @Query(value = """
                SELECT b.title, l.languages AS language, a.name AS authorName, a.birth_year AS birthYear, a.death_year AS deathYear
                FROM books b
                JOIN book_languages l ON b.id = l.book_id
                JOIN book_people bp ON b.id = bp.book_id
                JOIN author a ON bp.people_id = a.id
                WHERE l.languages = :language AND a.death_year >= :anoInicial AND a.birth_year <= :anoFinal
                ORDER BY b.title DESC
            """, nativeQuery = true)
    List<BookWithAuthorDTO> findByAutoresVivosNestePeriodo(Integer anoInicial, Integer anoFinal, String language);


    @Query(value = """
    SELECT new br.com.Challenger.LiterAlura.dto.AuthorDTO(
        a.name,
        a.birthYear,
        a.deathYear
    )
    FROM People a
    WHERE a.birthYear <= :endOfCentury AND a.deathYear >= :startOfCentury
    ORDER BY a.name DESC
""")
    List<AuthorDTO> findByAuthorInCentury(@Param("startOfCentury") Integer startOfCentury, @Param("endOfCentury") Integer endOfCentury);



}

