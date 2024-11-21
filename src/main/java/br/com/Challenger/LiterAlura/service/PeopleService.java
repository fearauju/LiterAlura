package br.com.Challenger.LiterAlura.service;

import br.com.Challenger.LiterAlura.dto.AuthorDTO;
import br.com.Challenger.LiterAlura.dto.BookWithAuthorDTO;
import br.com.Challenger.LiterAlura.model.People;
import br.com.Challenger.LiterAlura.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PeopleService {

    @Autowired
    private PeopleRepository peopleRepository;


    @Transactional
    public People saveAuthor(People author) {
        List<People> existingAuthor = peopleRepository.findByNameAuthor(author.getName());

        if (existingAuthor.isEmpty()) {
            return peopleRepository.save(author);
        } else {
            People managedAuthor = (People) existingAuthor;
            // Atualizar apenas campos necessários
            if (author.getBirthYear() != null && managedAuthor.getBirthYear() == null) {
                managedAuthor.setBirthYear(author.getBirthYear());
            }
            if (author.getDeathYear() != null && managedAuthor.getDeathYear() == null) {
                managedAuthor.setDeathYear(author.getDeathYear());
            }
            return managedAuthor; // Retorna o autor gerenciado
        }
    }


    public boolean existsByAuthor(String name) {
        return peopleRepository.existsByName(name);
    }

    public List<BookWithAuthorDTO> findBooksByLivingAuthorsBetweenYearsAndLanguage(Integer initialYear, Integer endYear, String language) {
        return peopleRepository.findByAutoresVivosNestePeriodo(initialYear, endYear, language);
    }

    public List<AuthorDTO> findAuthorsByCentury(Integer year) {
        int startOfCentury = (year / 100) * 100; // Normaliza para o início do século
        int endOfCentury = startOfCentury + 99; // Calcula o fim do século

        return peopleRepository.findByAuthorInCentury(startOfCentury, endOfCentury);
    }


    public List<People> findAllAuthors() {
        return peopleRepository.findAll();
    }

    public String normalizeName(String name) {
        // Remove vírgulas e inverte o nome se necessário
        if (name.contains(",")) {
            String[] parts = name.split(",");
            name = parts[1].trim() + " " + parts[0].trim();
        }
        // Converte para minúsculas e remove espaços extras
        return name.toLowerCase().trim();
    }

    public List<People> findByNameAuthor(String authorName) {
        return peopleRepository.findByNameAuthor(authorName);
    }
}


