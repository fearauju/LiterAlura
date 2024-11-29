package br.com.Challenger.LiterAlura.model;

import jakarta.persistence.*;
import java.util.*;


@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 1000)
    private String title;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_languages", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "languages")
    private Set<String> languages = new HashSet<>();  //O Hibernate permite carregar múltiplos fetch joins quando as coleções são do tipo Set em vez de List.

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "book_subjects", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "subjects")
    private Set<String> subjects = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(name = "copyright")
    private Boolean copyright;

    @Column(name = "download_count")
    private Integer downloadCount;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "book_people",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "people_id")
    )
    private List<People> authors = new ArrayList<>();

    public Book(){
    }

    public Book(String title, Set<String> languages, Set<String> subjects, boolean copyright, Integer downloadCount) {
        this.title = title;
        this.languages = languages;
        this.genre = Genre.fromSubjects(subjects).orElse(Genre.UNDEFINED);
        this.subjects = subjects;
        this.copyright = copyright;
        this.downloadCount = downloadCount;
    }

    // Getters e Setters


    public Genre getGenre() {
        return genre;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public Set<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<String> subjects) {
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

    public Set<String> getLanguages() {
        return languages;
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
        return String.format("""
                Title: %s
                Languages: %s
                Subjects: %s
                Copyright: %s
                Download count: %d
                """, title,
                String.join(", ", languages),
                String.join(", ", subjects),
                copyright != null && copyright ? "Has Copyright" : "Public Domain",
                downloadCount);
    }
}