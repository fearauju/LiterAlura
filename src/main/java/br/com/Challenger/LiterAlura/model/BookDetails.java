package br.com.Challenger.LiterAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDetails(
        String title,
        Set<String> languages, // também alterado de List para Set para realizar múltiplos Fetch Join
        @JsonAlias("copyright") boolean direitosAutorais,
        Integer download_count,
        Set<String> subjects, // também alterado de List para Set
        @JsonAlias("authors")
        List<PeopleDetails> authorsDetails){
}
