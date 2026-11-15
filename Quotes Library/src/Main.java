import java.sql.SQLException;

void main() {
    String book = IO.readln("Book Name : ");
    String quote = IO.readln("Quote     : ");
    String author = IO.readln("Author    : ");
    String category = IO.readln("Category  : ");

    Quote newQuote = new Quote(book, quote, author, category);

    try {
        new QuoteDOA().saveQuote(newQuote);
        IO.println("\nSaved: " + newQuote);
    } catch (SQLException e) {
        IO.println("\nCould not save quote: " + e.getMessage());
        e.printStackTrace();
    }

}
    

