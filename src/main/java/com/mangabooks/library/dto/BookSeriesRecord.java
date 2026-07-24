package com.mangabooks.library.dto;

import java.util.List;

public record BookSeriesRecord(
        String title
        , Integer totalVolumes
        , List<BookRecord> books
        , List<String> authors

                               ) {

    public BookSeriesRecord(String title, Integer totalVolumes){
        this(title,totalVolumes,null,null);
    }
    public BookSeriesRecord(String title, Integer totalVolumes, List<String> authors){
        this(title,totalVolumes,null, authors);
    }
}
