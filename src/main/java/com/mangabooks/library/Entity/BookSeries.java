package com.mangabooks.library.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class BookSeries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=100)
    private String title;

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "book_series")
    private List<Book> books;
    // Many-to-many with Author
    @ManyToMany
    @JoinTable(
            name = "author_series", // join table
            joinColumns = @JoinColumn(name = "series_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    @JsonManagedReference(value = "author_series")
    private List<Author> authors;

    @Column(name ="total_volumes")
    private Byte totalVolumes;


}
