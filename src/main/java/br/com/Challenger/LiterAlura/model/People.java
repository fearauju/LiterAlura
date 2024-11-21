package br.com.Challenger.LiterAlura.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "author")
public class People {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private Integer birthYear;
    private Integer deathYear;

    @ManyToMany(mappedBy = "authors", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Book> bookList = new ArrayList<>();

    public People() {}

    public People(String name, Integer birthYear, Integer deathYear) {
        this.name = name;
        this.birthYear = birthYear;
        this.deathYear = deathYear;
    }

    public People(Map<String, String> author) {
        this.name = author.get("name");
        this.birthYear = author.get("birth_year") != null ? Integer.parseInt(author.get("birth_year")) : null;
        this.deathYear = author.get("death_year") != null ? Integer.parseInt(author.get("death_year")) : null;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Book> getBookList() {
        return bookList;
    }

    public void setBookList(List<Book> bookList) {

        // Limpa as associações bidirecionais antigas
        for (Book book : this.bookList) {
            book.getAuthors().remove(this);
        }

        this.bookList.clear();

        // Atualiza a lista de livros e define a associação bidirecional
        if (bookList != null) {
            for (Book book : bookList) {
                this.addBook(book);
            }
        }
    }

    public void addBook(Book book) {
        if (!this.bookList.contains(book)) {
            this.bookList.add(book);
            book.getAuthors().add(this);
        }
    }

    @Override
    public String toString() {
        return
                "name: " + name +
                ", Birth year: " + birthYear +
                ", Death year: " + deathYear;
    }
}