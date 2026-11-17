import java.util.List;

void main() {
    QuoteDOA.createTable();

    while (true) {
        IO.println("\n--- Quoter! ---");
        IO.println("1. Add Quote details");
        IO.println("2. Quote search :");
        IO.println("3. Delete a Quote");
        IO.println("4. Update quote");
        IO.println("5. Exit");

        String choice = IO.readln("Choose an option: ");

        switch (choice) {
            case "1" -> {
                String book = IO.readln("Book Name :");
                String quote = IO.readln("The Quote Your Majesty :");
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
                    for (Quote q : quotes) {
                        IO.println(q.id());
                        IO.println(q.book());
                        IO.println(q.quote());
                        IO.println(q.author());
                        IO.println(q.category());
                    }
                }
            }

            case "3" -> {
                String input = IO.readln("Enter a Quote ID to delete : ");
                try {
                    int id = Integer.parseInt(input);
                    boolean del = QuoteDOA.deleteQuote(id);

                    if (del) {
                        IO.println("Quote deleted.");
                    } else {
                        IO.println("No Quote found with this id.");
                    }
                } catch (NumberFormatException e) {
                    IO.println("Enter a number.");
                }
            }

            case "4" -> {
                String input = IO.readln("Enter Quote ID to update: ");
                try {
                    int id = Integer.parseInt(input);
                    String book = IO.readln("Enter new book title: ");
                    String quote = IO.readln("Enter new quote: ");
                    String author = IO.readln("Enter new author: ");
                    String category = IO.readln("Enter new category: ");

                    Quote updatedQuote = new Quote(id, book, quote, author, category);
                    boolean updated = QuoteDOA.updateQuote(updatedQuote);

                    if (updated) {
                        IO.println("Quote updated successfully.");
                    } else {
                        IO.println("No quote found with that ID.");
                    }
                } catch (NumberFormatException e) {
                    IO.println("Invalid ID. Please enter a valid number.");
                }
            }

            case "5" -> {
                IO.println("Never say goodbye because goodbye means going away and going away means forgetting...");
                return;
            }

            default -> IO.println("Invalid option. Please try again.");
        }
    }
}
    

