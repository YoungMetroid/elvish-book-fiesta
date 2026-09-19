package com.mangabooks.library.service;

import com.mangabooks.library.Entity.Author;
import com.mangabooks.library.Entity.Book;
import com.mangabooks.library.Entity.BookSeries;
import com.mangabooks.library.dto.BookRecord;
import com.mangabooks.library.dto.BookSeriesRecord;
import com.mangabooks.library.exception.AuthorException;
import com.mangabooks.library.exception.ResourceNotFoundException;
import com.mangabooks.library.exception.VolumeException;
import com.mangabooks.library.repository.AuthorRepository;
import com.mangabooks.library.repository.BookRepository;
import com.mangabooks.library.repository.BookSeriesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import java.util.*;
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

    public List<BookSeries> searchBookSeries(String title){
        List<BookSeries> bs = bookSeriesRepository.findBookSeriesByName(title);
        return bs;
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
                && null != bsr.ownedVolumes()
                && !bsr.ownedVolumes().isEmpty()
        ){
            return true;
        }

        if(BookSeriesPayLoadType.VolumeRange.equals(payLoadType)
                && null !=bsr.startOwnedVolume()
                && null != bsr.endOwnedVolume()
                && bsr.startOwnedVolume() > 0
                && bsr.endOwnedVolume() > 0
        ){
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
                newBookSeries = createBookSeries(bsr);
                List<Book> bookList = createBooks(newBookSeries,0);
                newBookSeries.setBooks(bookList);
            }
        }
        return newBookSeries;
    }

    public BookSeries addBookSeriesByVolumeList(BookSeriesRecord bsr){
        if(isBookSeriesPayLoadOk(bsr,BookSeriesPayLoadType.VolumeList)){
            Optional<BookSeries> bookSeriesExist = bookSeriesRepository.findFirstBookSeriesByName(bsr.title());
            if(bookSeriesExist.isPresent()){
                return bookSeriesExist.get();
            }
            else{
                BookSeries newBookSeries = createBookSeries(bsr);
                List<Book> bookList = createBooks(newBookSeries,0);
                bookList = createBooks(bookList, bsr.ownedVolumes());
                newBookSeries.setBooks(bookList);
                return newBookSeries;
            }
        }
        throw new ResourceNotFoundException("The start/end volumes owned is either null or less than 1");
    }

    public BookSeries addBookSeriesByRange(BookSeriesRecord bsr){
        if(isBookSeriesPayLoadOk(bsr,BookSeriesPayLoadType.VolumeRange)){
            Optional<BookSeries> bookSeriesExist = bookSeriesRepository.findFirstBookSeriesByName(bsr.title());
            if(bookSeriesExist.isPresent()){
                return bookSeriesExist.get();
            }
            else{
                BookSeries newBookSeries = createBookSeries(bsr);
                List<Book> bookList = createBooks(newBookSeries
                        ,bsr.startOwnedVolume()
                        ,bsr.endOwnedVolume());
                newBookSeries.setBooks(bookList);
                return newBookSeries;
            }
        }
        throw new ResourceNotFoundException("The startOwnedVolume or endOwnedVolume is either null or empty");
    }

    public List<Book> addBooksToExistingSeries(List<BookRecord> bookRecordList){
        List<Book> processedBooks = new ArrayList<>();
        Optional<BookSeries> bs;
        for(BookRecord bookRecord : bookRecordList){
            Book book;
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
    public List<Book> addBooksToExistingSeriesByBookSeriesId(List<BookRecord> bookRecordList){
        List<Book> processedBooks = new ArrayList<>();
        Optional<BookSeries> bs;
        for(BookRecord bookRecord : bookRecordList){

            bs  = bookSeriesRepository.findById(bookRecord.seriesId());
            if(bs.isPresent()){
                Book book;
                List<Book> books = bs.get().getBooks();
                Optional<Book>optionalBook = books.stream()
                        .filter(x -> x.getVolume().equals(bookRecord.volume()))
                        .findFirst();
                if(optionalBook.isPresent()){
                    book = optionalBook.get();
                    book.setOwned((byte)1);
                    processedBooks.add(book);
                }
                else{
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
        }
        processedBooks = bookRepository.saveAll(processedBooks);
        return processedBooks;
    }
    public List<Author> findAuthors(List<String> authorNames){
        if(authorNames.isEmpty()){
            throw new AuthorException("The author List is Empty");
        }

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
        List<Book> bookList = new ArrayList<>();
        String authors = getConcatenatedAuthors(bookSeries.getAuthors());

        for(Byte i =0; i < bookSeries.getTotalVolumes(); i++){
            Book book = new Book();
            book.setAuthor(authors);
            book.setTitle(bookSeries.getTitle());
            book.setVolume((byte) (i+1));
            book.setSeries(bookSeries);
            book.setOwned(i+1 >= startVolume && i+1 <= endVolume ? (byte)1 :(byte)0);
            bookList.add(book);
        }
        bookList = bookRepository.saveAll(bookList);
        return bookList;
    }
    public List<Book> createBooksByRange(BookSeries bookSeries, Byte lastVolume){
        List<Book> bookList = new ArrayList<>();
        String authors = getConcatenatedAuthors(bookSeries.getAuthors());
        lastVolume++;
        for(; lastVolume <= bookSeries.getTotalVolumes(); lastVolume++){
            Book book = new Book();
            book.setAuthor(authors);
            book.setTitle(bookSeries.getTitle());
            book.setVolume(lastVolume);
            book.setSeries(bookSeries);
            book.setOwned((byte)0);
            bookList.add(book);
        }
        bookList = bookRepository.saveAll(bookList);
        return bookList;
    }

    public BookSeries createBookSeries( BookSeriesRecord bsr){
        BookSeries newBookSeries = new BookSeries();
        List<String> authorNames = bsr.authors();
        List<Author>authors = findAuthors(authorNames);

        newBookSeries.setAuthors(authors);
        newBookSeries.setTitle(bsr.title());
        newBookSeries.setTotalVolumes(bsr.totalVolumes());
        newBookSeries.setPublisher(bsr.publisher());
        newBookSeries.setPublisherOriginal(bsr.publisherOriginal());
        newBookSeries = bookSeriesRepository.save(newBookSeries);
        return newBookSeries;
    }

    public String getConcatenatedAuthors(List<Author> authorList){
        String authors = authorList.stream()
                .map(x->x.getName())
                .collect(Collectors.joining(", "));
        return authors;
    }

    public List<Book> addMissingVolumeEntriesToAllSeries(){
        List<BookSeries> bookSeries = bookSeriesRepository.findAll();
        List<Book> missingBooks = new ArrayList<>();
        for(BookSeries bs :bookSeries){
            List<Book> mbps = getMissingBooksPerSeries(bs);
            missingBooks.addAll(mbps);
        }
        missingBooks = bookRepository.saveAll(missingBooks);
        return missingBooks;
    }

    private List<Book> getMissingBooksPerSeries(BookSeries bs){
        List<Book> books = bookRepository.findBySeriesId(bs.getId());
        books = books.stream()
                .sorted(Comparator.comparing(Book::getVolume))
                .collect(Collectors.toList());

        if(books.isEmpty()){
            return Collections.emptyList();
        }

        List<Book> missingBooks = new ArrayList<>();
        Book newBook = new Book();
        int j = 0;
        newBook.setAuthor(books.getFirst().getAuthor());
        newBook.setTitle(books.getFirst().getTitle());
        newBook.setSeries(bs);
        newBook.setOwned((byte)0);

        for(int i = 0; i < bs.getTotalVolumes(); i++){
            if(j < books.size() && books.get(j).getVolume() == i+1){
                j++;
            }
            else{
                newBook.setVolume((byte)(i+1));
                missingBooks.add(new Book(newBook));
            }
        }
        return missingBooks;
    }


    public List<Book> updateVolumeCountAndAddVolumeEntries(List<BookSeriesRecord> bsrList){

        List<BookSeries> bookSeriesList = new ArrayList<>();
        List<Book> bookList = new ArrayList<>();
        for(BookSeriesRecord bookSeriesRecord: bsrList){
            Optional<BookSeries> obs = bookSeriesRepository.findById(bookSeriesRecord.id());
            if(obs.isPresent()){
                BookSeries bs = obs.get();
                if(bs.getTotalVolumes() <= bookSeriesRecord.totalVolumes() ){
                    throw new VolumeException("");
                }
                byte lastVolume = bs.getTotalVolumes();
                bs.setTotalVolumes(bookSeriesRecord.totalVolumes());
                bookSeriesList.add(bs);
                List<Book> newBooks = createBooksByRange(bs,lastVolume);
                bookList.addAll(newBooks);
            }
        }
        bookSeriesRepository.saveAll(bookSeriesList);
        return bookList;
    }
}
