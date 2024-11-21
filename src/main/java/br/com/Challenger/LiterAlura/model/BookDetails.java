package br.com.Challenger.LiterAlura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BookDetails(
        String title,
        List<String> languages,
        @JsonAlias("copyright") boolean direitosAutorais,
        Integer download_count,
        List<String> subjects,
        PeopleDetails authorsDetails){
}
