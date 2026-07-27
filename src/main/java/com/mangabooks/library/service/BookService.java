package com.mangabooks.library.service;

import com.mangabooks.library.Entity.Book;
import com.mangabooks.library.exception.ResourceNotFoundException;
import com.mangabooks.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book getBookById(Long id){
        Book book = bookRepository
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Book not found with id: " + id));
        return book;
    }
    public Book getBookByNameAndVolume(String title, Byte volume){
        Book book = bookRepository
                .findFirstBookByNameAndVolume(title,volume)
                .orElseThrow(()->new ResourceNotFoundException("Book " + title + " volume: " +
                        volume + " not found"));
        return book;
    }
}
