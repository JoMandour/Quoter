public record Quote(int id, String book, String quote, String author, String category) {
    
    public Quote(String book, String quote, String author, String category) {
        this(0, book, quote, author, category);
    }
}