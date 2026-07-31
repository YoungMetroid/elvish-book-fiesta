package com.mangabooks.library.controller;


import com.mangabooks.library.Entity.Book;
import com.mangabooks.library.Entity.BookSeries;
import com.mangabooks.library.dto.BookRecord;
import com.mangabooks.library.dto.BookSeriesRecord;
import com.mangabooks.library.service.BookSeriesService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        List<BookSeriesRecord> series = bookSeriesService.getAllBookSeries();
        return ResponseEntity.ok(series);
    }

    @GetMapping("/all")
    public ResponseEntity<List<BookSeries>> getBookAll(){
        return ResponseEntity.ok(bookSeriesService.getAll());
    }


    @PostMapping("/addBookSeriesLov")
    public ResponseEntity<BookSeries> addBookSeriesListOfVolumes(@RequestBody BookSeriesRecord bookSeriesRecord){
        BookSeries bookSeries = bookSeriesService.addBookSeriesListofVolumes(bookSeriesRecord);
        return ResponseEntity.ok(bookSeries);
    }

    @PostMapping("/addBooksToExistingSeries")
    public ResponseEntity<List<Book>> addBooksToExistingSeries(@RequestBody List<BookRecord> bookRecords){
        List<Book> books = bookSeriesService.addBooksToExistingSeries(bookRecords);
        return ResponseEntity.ok(books);
    }

}
