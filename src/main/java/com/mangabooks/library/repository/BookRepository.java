package com.mangabooks.library.repository;

import com.mangabooks.library.Entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book,Long> {

    @Query("SELECT b FROM Book b WHERE LOWER(REPLACE(b.title, ' ','')) " +
            "= LOWER(REPLACE(:name, ' ','')) AND b.volume = :volume")
    Optional<Book> findFirstBookByNameAndVolume(@Param("name")String name, @Param("volume") Byte volume);

    @Query("SELECT b FROM Book b WHERE LOWER(REPLACE(b.title, ' ','')) " +
            "= LOWER(REPLACE(:name, ' ',''))" )
    List<Book> findByName(@Param("name")String name);


    @Modifying
    @Query("UPDATE Book b SET b.owned = 1 WHERE b.title = :name AND b.volume BETWEEN :start AND :end")
    int markOwned(@Param("name")String name, @Param("start") Byte start, @Param("end") Byte end);
}
