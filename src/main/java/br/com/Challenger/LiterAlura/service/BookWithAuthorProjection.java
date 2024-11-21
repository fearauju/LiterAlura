package br.com.Challenger.LiterAlura.service;

public interface BookWithAuthorProjection {

    String getBookTitle();
    String getLanguages();
    String getSubjects();
    Boolean getCopyright();
    String getAuthorName();
    Integer getBirthYear();
    Integer getDeathYear();
}
