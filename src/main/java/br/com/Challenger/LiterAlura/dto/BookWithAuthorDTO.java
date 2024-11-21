package br.com.Challenger.LiterAlura.dto;

import java.util.List;

//Transformei de record para DTO. Por que? problemas com a JPQL, falhava em reconhecer o contrutor, deve ser porque as consultava
//manipulava muitas tabelas, porque que inclui coleções, não sei, mas essa foi a solução mais fácil e vável que encontrei.

public class BookWithAuthorDTO {
    private String bookTitle;
    private List<String> languages;
    private List<String> subjects;
    private Boolean copyright;
    private String authorName;
    private Integer birthYear;
    private Integer deathYear;

    public BookWithAuthorDTO(){}

    // Construtor com todos os atributos
    public BookWithAuthorDTO(String bookTitle, List<String> languages, List<String> subjects, Boolean copyright, String authorName, Integer birthYear, Integer deathYear) {
        this.bookTitle = bookTitle;
        this.languages = languages;
        this.subjects = subjects;
        this.copyright = copyright;
        this.authorName = authorName;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
    }

    // Getters e Setters
    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public Boolean getCopyright() {
        return copyright;
    }

    public void setCopyright(Boolean copyright) {
        this.copyright = copyright;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }
}
