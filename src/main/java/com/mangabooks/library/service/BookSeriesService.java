package com.mangabooks.library.service;

import com.mangabooks.library.Entity.Author;
import com.mangabooks.library.Entity.Book;
import com.mangabooks.library.Entity.BookSeries;
import com.mangabooks.library.dto.BookRecord;
import com.mangabooks.library.dto.BookSeriesRecord;
import com.mangabooks.library.exception.AuthorException;
import com.mangabooks.library.exception.ResourceNotFoundException;
import com.mangabooks.library.repository.AuthorRepository;
import com.mangabooks.library.repository.BookRepository;
import com.mangabooks.library.repository.BookSeriesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

enum BookSeriesPayLoadType {
    NoVolumes, BookList, VolumeList, VolumeRange
}

@EnableTransactionManagement
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
                .collect(toList());
    }

    public List<BookSeries> getAll(){
        return this.bookSeriesRepository.findAll();
    }

    public boolean isBookSeriesPayLoadOk(BookSeriesRecord bsr, BookSeriesPayLoadType payLoadType){
        if(bsr.totalVolumes() <= 0  ||  bsr.authors().isEmpty() || bsr.authors().getFirst().isBlank()){
            throw new ResourceNotFoundException("Volume Count must be 1 or higher" +
                    "You have to include 1 or more authors" +
                    "Authors field cannot be blank or empty");
        }
        if(BookSeriesPayLoadType.NoVolumes.equals(payLoadType)){
            return true;
        }

        if(BookSeriesPayLoadType.VolumeList.equals(payLoadType)
                && null != bsr.ownedVolumes()){
            return true;
        }

        if(BookSeriesPayLoadType.VolumeRange.equals(payLoadType)
                && null !=bsr.startOwnedVolume()
                && null != bsr.endOwnedVolume()){
            return true;
        }

        if(BookSeriesPayLoadType.BookList.equals(payLoadType) && null != bsr.books()){
            return true;
        }

        return false;
    }

    public BookSeries addBookSeriesNoOwnedVolumes(BookSeriesRecord bsr){
        BookSeries newBookSeries = new BookSeries();
        if(isBookSeriesPayLoadOk(bsr,BookSeriesPayLoadType.NoVolumes)){
            Optional<BookSeries> bookSeriesExist = bookSeriesRepository.findFirstBookSeriesByName(bsr.title());
            if(bookSeriesExist.isPresent()){
                return bookSeriesExist.get();
            }
            else{
                List<String> authorNames = bsr.authors();
                List<Author>authors = findAuthors(authorNames);

                newBookSeries.setAuthors(authors);
                newBookSeries.setTitle(bsr.title());
                newBookSeries.setTotalVolumes(bsr.totalVolumes());
                newBookSeries = bookSeriesRepository.save(newBookSeries);

                List<Book> bookList = createBooks(newBookSeries,0);
                newBookSeries.setBooks(bookList);
            }
        }
        return newBookSeries;
    }

    public BookSeries addBookSeriesListofVolumes(BookSeriesRecord bsr){
        BookSeries newBookSeries = new BookSeries();
        if(isBookSeriesPayLoadOk(bsr,BookSeriesPayLoadType.NoVolumes)){
            Optional<BookSeries> bookSeriesExist = bookSeriesRepository.findFirstBookSeriesByName(bsr.title());
            if(bookSeriesExist.isPresent()){
                return bookSeriesExist.get();
            }
            else{
                List<String> authorNames = bsr.authors();
                List<Author>authors = findAuthors(authorNames);

                newBookSeries.setAuthors(authors);
                newBookSeries.setTitle(bsr.title());
                newBookSeries.setTotalVolumes(bsr.totalVolumes());
                newBookSeries = bookSeriesRepository.save(newBookSeries);

                List<Book> bookList = createBooks(newBookSeries,0);
                bookList = createBooks(bookList, bsr.ownedVolumes());
                newBookSeries.setBooks(bookList);
            }
        }
        return newBookSeries;
    }

    /*
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

                List<Book> books;
                //ownedVolumes has to be null and the startowned as well
                if(null == bookSeriesRecord.ownedVolumes() && null == bookSeriesRecord.startOwnedVolume()){
                    books = createBooks(newBookSeries,1);
                }
                //if start owned is filled out then create books in the range
                else if(null != bookSeriesRecord.startOwnedVolume() && null != bookSeriesRecord.endOwnedVolume()){
                    books = createBooks(newBookSeries
                            ,bookSeriesRecord.startOwnedVolume()
                            ,bookSeriesRecord.endOwnedVolume());
                }
                //if ownedVolumes List is filled out then create those books
                else {
                    books = createBooks(newBookSeries, bookSeriesRecord.ownedVolumes());
                }
                newBookSeries.setBooks(books);

                return newBookSeries;
            }
            return null;
        }
    }
    */

    public List<Book> addBooksToExistingSeries(List<BookRecord> bookRecordList){
        List<Book> processedBooks = new ArrayList<>();
        Optional<BookSeries> bs;
        Book book;
        for(BookRecord bookRecord : bookRecordList){
            Optional<Book> bookFound = bookRepository.findFirstBookByNameAndVolume(bookRecord.title()
                    ,bookRecord.volume());
            if(!bookFound.isPresent()){
                //Create the book entry if it does not exist
                //Should not occur very often since when creating the series
                //It should add all the book entries
                bs = bookSeriesRepository.findFirstBookSeriesByName(bookRecord.title());
                if(bs.isPresent()){
                    book = new Book();
                    book.setVolume(bookRecord.volume());
                    book.setTitle(bookRecord.title());
                    String authors = bs.get().getAuthors().stream()
                            .map(x->x.getName())
                            .collect(Collectors.joining(", "));
                    book.setAuthor(authors);
                    book.setOwned((byte) 1);
                    book.setSeries(bs.get());
                    processedBooks.add(book);
                }
            }
            else{
                //This will update the book entry to saying that we own it now.
                book = bookFound.get();
                book.setOwned((byte)1);
                processedBooks.add(book);
            }
        }
        processedBooks = bookRepository.saveAll(processedBooks);
        return processedBooks;
    }
    public List<Author> findAuthors(List<String> authorNames){
        List<Author> authorList = new ArrayList<>();
        if(authorNames.isEmpty()){
            throw new AuthorException("The author info is missing");
        }

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

    public List<Book> createBooks(BookSeries bookSeries, int owned){
        List<Book> books = new ArrayList<>();
        String authors = getConcatenatedAuthors(bookSeries.getAuthors());

        for(Byte i =0; i < bookSeries.getTotalVolumes(); i++){
            Book book = new Book();
            book.setAuthor(authors);
            book.setTitle(bookSeries.getTitle());
            book.setVolume((byte) (i+1));
            book.setSeries(bookSeries);
            book.setOwned((byte) owned);
            books.add(book);
        }
        books = bookRepository.saveAll(books);
        return books;
    }
    public List<Book> createBooks(List<Book> bookList, List<Byte> ownedVolumes){
        ownedVolumes =  ownedVolumes.stream().distinct().sorted().toList();
        bookList = bookList.stream().distinct().sorted(Comparator.comparing(Book::getVolume)).toList();

        for(int i = 0; i < ownedVolumes.size() ; i++){
            byte volume = ownedVolumes.get(i);

            if(volume-1 < bookList.size()  &&  bookList.get(volume-1).getVolume() == volume){
                bookList.get(volume-1).setOwned((byte)1);
            }
        }
        bookList = bookRepository.saveAll(bookList);
        return bookList;
    }
    public List<Book> createBooks(BookSeries bookSeries, Byte startVolume, Byte endVolume){
        List<Book> books = new ArrayList<>();
        String authors = getConcatenatedAuthors(bookSeries.getAuthors());

        for(Byte i =0; i < bookSeries.getTotalVolumes(); i++){
            Book book = new Book();
            book.setAuthor(authors);
            book.setTitle(bookSeries.getTitle());
            book.setVolume((byte) (i+1));
            book.setSeries(bookSeries);
            book.setOwned(i+1 >= startVolume && i+1 <= endVolume ? (byte)1 :(byte)0);
            books.add(book);
        }
        books = bookRepository.saveAll(books);
        return books;
    }

    public String getConcatenatedAuthors(List<Author> authorList){
        String authors = authorList.stream()
                .map(x->x.getName())
                .collect(Collectors.joining(", "));
        return authors;
    }




}
