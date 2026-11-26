package com.quoter.model;

import java.util.Objects;

public record Quote(int id, String book, String quote, String author, String category) {

    public static final String UNKNOWN_BOOK = "Unknown Book";
    public static final String UNKNOWN_AUTHOR = "Unknown Author";
    public static final String DEFAULT_CATEGORY = "General";

    public Quote {
        book = Objects.requireNonNull(book, "book");
        quote = Objects.requireNonNull(quote, "quote");
        author = Objects.requireNonNull(author, "author");
        category = Objects.requireNonNull(category, "category");
    }

    public Quote(String book, String quote, String author, String category) {
        this(0, book, quote, author, category);
    }
}
