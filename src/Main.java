void main() {
    String book = IO.readln("Book Name : ");
    String quote = IO.readln("Quote     : ");
    String author = IO.readln("Author    : ");
    String category = IO.readln("Category  : ");

    Quote newQuote = new Quote(book, quote, author, category);

    IO.println("\nCreated: " + newQuote);
}
    

