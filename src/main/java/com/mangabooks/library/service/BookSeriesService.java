package com.mangabooks.library.service;

import com.mangabooks.library.Entity.BookSeries;
import com.mangabooks.library.dto.BookSeriesRecord;
import com.mangabooks.library.repository.BookSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookSeriesService {

    private final BookSeriesRepository bookSeriesRepository;

    @Autowired
    public BookSeriesService(BookSeriesRepository bookSeriesRepository){
        this.bookSeriesRepository = bookSeriesRepository;
    }

    public List<BookSeriesRecord> getAllBookSeries(){
        return this.bookSeriesRepository.findAll().stream().map(b -> new BookSeriesRecord(b.getTitle(),b.getTotalVolumes())).collect(Collectors.toList());
    }

    public List<BookSeries> getAll(){
        return this.bookSeriesRepository.findAll();
    }

    public BookSeries addBookSeries(BookSeries bookSeries){

        Optional<BookSeries> bookSeriesExist = bookSeriesRepository.findFirstBookSeriesByName(bookSeries.getTitle());
        if(bookSeriesExist.isPresent()){
            return bookSeries;
        }
        else{

            bookSeries = bookSeriesRepository.save(bookSeries);
            return bookSeries;
        }

    }
}
