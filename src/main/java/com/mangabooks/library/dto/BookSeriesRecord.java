package com.mangabooks.library.dto;

import java.util.List;

public record BookSeriesRecord(
        String title
        , Byte totalVolumes
        , List<BookRecord> books
        , List<String> authors
        , List<Byte> ownedVolumes
                               ) {

    public BookSeriesRecord(String title, Byte totalVolumes){
        this(title,totalVolumes,null,null,null);
    }
    public BookSeriesRecord(String title, Byte totalVolumes, List<String> authors){
        this(title,totalVolumes,null, authors,null);
    }
    public BookSeriesRecord(String title, Byte totalVolumes, List<String> authors, List<Byte> ownedVolumes){
        this(title,totalVolumes,null, authors,ownedVolumes);
    }
}
