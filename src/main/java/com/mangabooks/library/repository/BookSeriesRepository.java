package com.mangabooks.library.repository;

import com.mangabooks.library.Entity.BookSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookSeriesRepository extends JpaRepository<BookSeries,Long> {

    @Query("SELECT bs FROM BookSeries bs WHERE LOWER(REPLACE(bs.title, ' ','')) " +
            "= LOWER(REPLACE(:name, ' ',''))")
    Optional<BookSeries> findFirstBookSeriesByName(@Param("name")String name);

    @Query("SELECT b from BookSeries b WHERE LOWER(REPLACE(b.title, ' ','')) "+
            "LIKE LOWER(CONCAT('%', REPLACE(:name, ' ',''), '%')) ")
    List<BookSeries> findBookSeriesByName(@Param("name")String name);

}
