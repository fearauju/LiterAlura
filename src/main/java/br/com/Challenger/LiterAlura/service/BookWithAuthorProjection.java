package br.com.Challenger.LiterAlura.service;

public interface BookWithAuthorProjection {
    // Interface para projeções ajudou a evitar carregamento excessivo de dados (Lazy Loading vs. Eager Loading),
    // especialmente ao lidar com relações complexas (N -> N) entre entidades

    String getBookTitle();
    String getLanguages();
    String getSubjects();
    Boolean getCopyright();
    String getAuthorName();
    Integer getBirthYear();
    Integer getDeathYear();
    Integer getDownloadCount();
    String getGenre();
}
