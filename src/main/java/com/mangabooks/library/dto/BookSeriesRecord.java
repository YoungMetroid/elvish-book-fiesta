package com.mangabooks.library.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookSeriesRecord(
        @NotBlank String title
        ,@NotNull Byte totalVolumes
        , Byte startOwnedVolume
        , Byte endOwnedVolume
        , List<BookRecord> books
        ,@NotNull List<String> authors
        , List<Byte> ownedVolumes
                               ) {

    public BookSeriesRecord(String title, Byte totalVolumes){
        this(title,totalVolumes,
                null,null, null,null, null);
    }
    public BookSeriesRecord(String title
            ,Byte totalVolumes
            ,List<String> authors){
        this(title,totalVolumes
                ,null, null,null,
                authors
                ,null);
    }
    public BookSeriesRecord(String title
            ,Byte totalVolumes
            ,List<String> authors
            ,List<Byte> ownedVolumes){
        this(title,totalVolumes,
                null, null, null,
                authors, ownedVolumes);
    }
    public BookSeriesRecord(String title
            ,Byte totalVolumes
            ,Byte startOwnedVolume
            ,Byte endOwnedVolume
            ,List<String> authors){
        this(title,totalVolumes
                ,startOwnedVolume,endOwnedVolume
                ,null, authors
                ,null);
    }

}
