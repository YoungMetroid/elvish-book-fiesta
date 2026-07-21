package com.mangabooks.library.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=100)
    private String title;

    private Byte volume;

    @Column(length=100)
    private String author;

    @ManyToOne
    @JoinColumn(name = "series_id")
    @JsonBackReference
    private BookSeries series;


}
