package com.mangabooks.library.dto;

public record BookRecord(
        String title
        ,Byte volume
        ,String author
        ,Boolean owned
) {
}
