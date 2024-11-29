package br.com.Challenger.LiterAlura.service;

import br.com.Challenger.LiterAlura.dto.AuthorDTO;
import br.com.Challenger.LiterAlura.model.People;
import br.com.Challenger.LiterAlura.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PeopleService {

    @Autowired
    private PeopleRepository peopleRepository;

    @Transactional
    public People saveOrUpdateAuthor(People author) {
        List<People> existingAuthors = peopleRepository.findByNameContaining(author.getName());

        if (existingAuthors.isEmpty()) {
            return peopleRepository.save(author);
        }

        // Atualiza apenas campos não nulos
        People managedAuthor = existingAuthors.get(0);
        if (author.getBirthYear() != null && managedAuthor.getBirthYear() == null) {
            managedAuthor.setBirthYear(author.getBirthYear());
        }
        if (author.getDeathYear() != null && managedAuthor.getDeathYear() == null) {
            managedAuthor.setDeathYear(author.getDeathYear());
        }
        return managedAuthor;
    }

    public String normalizeName(String name) {
        if (name.contains(",")) {
            String[] parts = name.split(",");
            name = parts[1].trim() + " " + parts[0].trim();
        }
        return name.toLowerCase().trim();
    }

    public List<People> findByNameContaining(String normalizedInputName) {
        return peopleRepository.findByNameContaining(normalizedInputName);
    }

    public List<People> listAllAuthors() {
            return peopleRepository.findAllAuthors();
    }

    public List<AuthorDTO> findAuthorsByCentury(Integer year) {
        int startOfCentury = (year / 100) * 100; // Normaliza para o início do século
        int endOfCentury = startOfCentury + 99; // Calcula o fim do século

        return peopleRepository.findByAuthorInCentury(startOfCentury, endOfCentury);
    }
}
