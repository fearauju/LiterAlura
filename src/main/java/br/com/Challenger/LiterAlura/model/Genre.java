package br.com.Challenger.LiterAlura.model;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;


//A maioria está como ficção porque na API foi atribuído dessa forma, principalmente em obras brasileiras, então
//embora não faça sentido, algumas obras estarão com gênero errado sendo atribuído, esses dados poderiam ser tratados no próprio banco de dados posteriormente.

public enum Genre {
    ART("Art"),
    AUTOBIOGRAPHY("Autobiography"), // Autobiografia
    BIOGRAPHY("Biography"), // Biografia
    BUSINESS("Business"),
    CHILDRENS("Childrens"), // Literatura infantil
    COOKING("Cooking"),
    DRAMA("Drama"),
    ECONOMICS("Economics"),
    EDUCATION("Education"), // Educacional
    ESSAY("Essay"), // Ensaios
    FANTASY("Fantasy"),
    FICTION("Fiction"), // Ficção
    HEALTH("Health"),
    HISTORICAL("Historical"), // Histórico
    HORROR("Horror"),
    HUMOR("Humor"),
    MEMOIR("Memoir"), // Memórias
    MUSIC("Music"),
    MYSTERY("Mystery"), // Mistério
    NON_FICTION("Non-Fiction"), // Não ficção
    PHILOSOPHY("Philosophy"),
    POETRY("Poetry"), // Poesia
    POLITICS("Politics"),
    PSYCHOLOGY("Psychology"),
    RELIGION("Religion"),
    ROMANCE("Romance"),
    SCIENCE("Science"),
    SCIENCE_FICTION("Science-Fiction"), // Ficção científica
    SELF_HELP("Self-Help"),
    SPIRITUALITY("Spirituality"),
    SPORT("Sport"),
    TECHNOLOGY("Technology"),
    THRILLER("Thriller"), // Suspense
    TRAVEL("Travel"),
    UNDEFINED("undefined"), // Gênero não encontrado. Na API gêneros são misturados com assuntos, se houver algum indefinido pode ser atualizado no banco de dados e adicionar esse novo gênero aqui posteriormente.
    YOUNG_ADULT("Young-Adult"); // Jovens adultos

    private final String categoryGenre;

    private Genre(String categoryGenre) {
        this.categoryGenre = categoryGenre;
    }

    public String getCategoryGenre() {
        return categoryGenre;
    }

    public static Optional<Genre> fromString(String text) {
        return Arrays.stream(Genre.values())
                .filter(genre -> text.equalsIgnoreCase(genre.categoryGenre))
                .findFirst();
    }

    public static Optional<Genre> fromSubjects(Set<String> subjects) {
        for (String subject : subjects) {
            for (Genre genre : Genre.values()) {
                if (subject.toLowerCase().contains(genre.categoryGenre.toLowerCase())) {
                    return Optional.of(genre);
                }
            }
        }
        return Optional.empty();
    }
}