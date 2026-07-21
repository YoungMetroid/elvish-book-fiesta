package com.mangabooks.library.controller;


import com.mangabooks.library.Entity.BookSeries;
import com.mangabooks.library.dto.BookSeriesRecord;
import com.mangabooks.library.service.BookSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("api/bookseries")
public class BookSeriesController {

    private final BookSeriesService bookSeriesService;

    @Autowired
    public BookSeriesController(BookSeriesService bookSeriesService){
        this.bookSeriesService = bookSeriesService;
    }

    @GetMapping
    public ResponseEntity<List<BookSeriesRecord>> getBookSeries(){
        return ResponseEntity.ok(bookSeriesService.getAllBookSeries());
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookSeries>> getBookAll(){
        return ResponseEntity.ok(bookSeriesService.getAll());
    }



}
