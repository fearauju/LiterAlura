package br.com.Challenger.LiterAlura.repository;

import br.com.Challenger.LiterAlura.dto.AuthorDTO;
import br.com.Challenger.LiterAlura.model.People;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PeopleRepository extends JpaRepository<People, Long> {

    @Query("SELECT a FROM People a")
    List<People> findAllAuthors();


    @Query("SELECT a FROM People a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<People> findByNameContaining(@Param("name") String name);

    @Query("""
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


