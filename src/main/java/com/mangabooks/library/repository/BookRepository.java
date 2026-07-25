package com.mangabooks.library.repository;

import com.mangabooks.library.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("SELECT b FROM BookSeries b WHERE LOWER(REPLACE(b.title, ' ','')) " +
            "= LOWER(REPLACE(:name, ' ','')) AND b.volume = :volume")
    Optional<Book> findFirstBookByNameAndVolume(@Param("name")String name, @Param("volume") Byte volume);
}
