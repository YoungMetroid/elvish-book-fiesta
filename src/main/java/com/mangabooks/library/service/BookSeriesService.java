package com.mangabooks.library.service;

import com.mangabooks.library.Entity.Author;
import com.mangabooks.library.Entity.Book;
import com.mangabooks.library.Entity.BookSeries;
import com.mangabooks.library.dto.BookRecord;
import com.mangabooks.library.dto.BookSeriesRecord;
import com.mangabooks.library.repository.AuthorRepository;
import com.mangabooks.library.repository.BookRepository;
import com.mangabooks.library.repository.BookSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookSeriesService {

    private final BookSeriesRepository bookSeriesRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    @Autowired
    public BookSeriesService(BookSeriesRepository bookSeriesRepository,
                             BookRepository bookRepository,
                             AuthorRepository authorRepository){
        this.bookSeriesRepository = bookSeriesRepository;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;

    }

    public List<BookSeriesRecord> getAllBookSeries(){
        return this.bookSeriesRepository.findAll()
                .stream()
                .map(b -> new BookSeriesRecord(b.getTitle(),b.getTotalVolumes()))
                .collect(Collectors.toList());
    }

    public List<BookSeries> getAll(){
        return this.bookSeriesRepository.findAll();
    }

    public BookSeries addBookSeries(BookSeriesRecord bookSeriesRecord){
        Optional<BookSeries> bookSeriesExist = bookSeriesRepository.findFirstBookSeriesByName(bookSeriesRecord.title());
        if(bookSeriesExist.isPresent()){
            return bookSeriesExist.get();
        }
        else{
            if(null !=bookSeriesRecord.title()
                    && !bookSeriesRecord.title().isBlank()
                    && bookSeriesRecord.totalVolumes() > 0) {
            //Check if the author exist if it does not create the author
            //Once the author is created get that author list and add it to the bookSeries.

                BookSeries newBookSeries = new BookSeries();
                List<String> authorNames = bookSeriesRecord.authors();
                List<Author>authors = findAuthors(authorNames);

                newBookSeries.setAuthors(authors);
                newBookSeries.setTitle(bookSeriesRecord.title());
                newBookSeries.setTotalVolumes(bookSeriesRecord.totalVolumes());
                newBookSeries = bookSeriesRepository.save(newBookSeries);

            //Now create the volumes since you have the series name
            //The amount of volumes
            //And the list of authors
                List<Book> books = createBooks(newBookSeries);
                newBookSeries.setBooks(books);
                return newBookSeries;
            }
            //throw newNotFoundException
            return null;
        }
    }

    public List<Author> findAuthors(List<String> authorNames){
        List<Author> authorList = new ArrayList<>();

        for(String name:authorNames){
            Optional<Author> a = authorRepository.findByName(name);
            if(a.isPresent()){
                authorList.add(a.get());
            }
            else{
                //Create Author
                Author createdAuthor = new Author();
                createdAuthor.setName(name);
                createdAuthor = authorRepository.save(createdAuthor);
                authorList.add(createdAuthor);
            }
        }
        return authorList;
    }

    public List<Book> createBooks(BookSeries bookSeries){
        List<Book> books = new ArrayList<>();
        String authors = bookSeries.getAuthors().stream()
                .map(x->x.getName())
                .collect(Collectors.joining(", "));

        for(int i =0; i < bookSeries.getTotalVolumes(); i++){
            Book book = new Book();
            book.setAuthor(authors);
            book.setTitle(bookSeries.getTitle());
            book.setVolume((byte) (i+1));
            book.setSeries(bookSeries);

            books.add(book);
        }
        bookRepository.saveAll(books);
        return books;
    }

}
