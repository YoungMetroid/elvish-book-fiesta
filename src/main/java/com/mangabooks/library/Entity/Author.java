package com.mangabooks.library.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=100)
    private String name;

    @ManyToMany(mappedBy = "authors")
    @JsonBackReference
    private List<BookSeries> series;
}
