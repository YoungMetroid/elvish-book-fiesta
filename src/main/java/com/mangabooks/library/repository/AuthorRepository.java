package com.mangabooks.library.repository;

import com.mangabooks.library.Entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author,Long> {
    @Query("Select a from Author a where LOWER(TRIM(a.name)) = LOWER(TRIM(:name))")
    Optional<Author> findByName(@Param("name") String name);
}
