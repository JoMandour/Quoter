import java.util.List;
void main() {
    QuoteDOA.createTable();
    
    while (true) {
            IO.println("\n--- Quoter! ---");
            IO.println("1. Add Quote details");
            IO.println("2. Quote search :");
            IO.println("4. Exit");

            String choice = IO.readln("Choose an option: ");

            switch (choice) {
                case "1" -> {
                    String book = IO.readln("Book Name :");
                    String quote =IO.readln("The Quote Your Majesty :");
                    String author = IO.readln("Author name :");
                    String category = IO.readln("Category :");

                    Quote newQuote = new Quote(book, quote, author, category);
                    QuoteDOA.insertData(newQuote);
                }

                case "2" -> {
                    String keyword = IO.readln("Enter a keyword: ");
                    List<Quote> quotes = QuoteDOA.searchQuotes(keyword);

                    if (quotes.isEmpty()) {
                        IO.println("No Quote found.");
                    } else {
                        for(Quote q : quotes) {
                           IO.println(q.id());
                           IO.println(q.book());
                           IO.println(q.quote()); 
                           IO.println(q.author()); 
                           IO.println(q.category());
                        }
                    }


                }

                default -> IO.println("Invalid option. Please try again.");
            }
        }

}
    

