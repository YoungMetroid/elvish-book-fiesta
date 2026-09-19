package com.mangabooks.library.dto;
import java.util.List;

public record BookSeriesRecord(
        Long id
        , String title
        , String publisher
        , String publisherOriginal
        , Byte totalVolumes
        , Byte startOwnedVolume
        , Byte endOwnedVolume
        , List<BookRecord> books
        ,
        List<String> authors
        , List<Byte> ownedVolumes
                               ) {

    public BookSeriesRecord(String title, Byte totalVolumes){
        this(null, title,null,null
                ,totalVolumes,null,null
                , null,null, null);
    }
}
