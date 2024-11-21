package br.com.Challenger.LiterAlura.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 1000)
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "languages")
    private List<String> languages = new ArrayList<>();

    @Column(name = "copyright")
    private Boolean copyright;

    @Column(name = "download_count")
    private Integer downloadCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_subjects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subjects")
    private List<String> subjects;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "book_people",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "people_id")
    )
    private List<People> authors = new ArrayList<>();

    public Book() {
    }

    public Book(String title, List<String> languages, List<String> subjects, Boolean copyright, Integer downloadCount) {
        this.title = title;
        this.languages = languages;
        this.subjects = subjects;
        this.copyright = copyright;
        this.downloadCount = downloadCount;
    }

    // Getters e Setters

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public Boolean getCopyright() {
        return copyright;
    }

    public void setcopyright(Boolean copyright) {
        this.copyright = copyright;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public List<People> getAuthors() {
        return authors;
    }

    public void addAuthor(People author) {
        if (!this.authors.contains(author)) {
            this.authors.add(author);
            author.getBookList().add(this);
        }
    }

    public void setAuthors(List<People> authors) {
        this.authors.clear();
        if (authors != null) {
            for (People author : authors) {
                this.addAuthor(author);
            }
        }
    }

    @Override
    public String toString() {
        String titleStr = title != null ? title : "";
        List<String> languagesList = languages != null ? languages : Collections.emptyList();
        boolean copyrightBool = copyright != null && copyright;

        return "Title: " + titleStr +
                "\n" +
                "Langyages: " + String.join(", ", languagesList) +
                "\n" +
                "Subjects: " + String.join(", ", subjects) +
                "\n" +
                "Copyright: " + (copyrightBool ? "Has Copyright" : "Public domain") +
                "\n" +
                "Download count: " + downloadCount +
                "\n" +
                "\n";
    }
}