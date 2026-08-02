package com.mangabooks.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookSeriesRecord(
        @NotBlank String title
        ,@NotBlank String publisher
        ,@NotBlank String publisherOriginal
        ,@NotNull Byte totalVolumes
        , Byte startOwnedVolume
        , Byte endOwnedVolume
        , List<BookRecord> books
        ,@NotNull List<String> authors
        , List<Byte> ownedVolumes
                               ) {

    public BookSeriesRecord(String title, Byte totalVolumes){
        this(title,null,null
                ,totalVolumes,null,null
                , null,null, null);
    }
}
