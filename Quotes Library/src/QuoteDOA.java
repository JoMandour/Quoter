import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class QuoteDOA {
    private static final String JDBC_URL = "jdbc:sqlite:L:/Quotes library/Quotes Library/quotes.db";

    void saveQuote(Quote quote) throws SQLException {
        try (Connection connection = DriverManager.getConnection(JDBC_URL);
                Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS quotes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        book TEXT NOT NULL,
                        quote TEXT NOT NULL,
                        author TEXT NOT NULL,
                        category TEXT NOT NULL
                    )
                    """);

            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO quotes (book, quote, author, category) VALUES (?, ?, ?, ?)")) {
                insert.setString(1, quote.book());
                insert.setString(2, quote.quote());
                insert.setString(3, quote.author());
                insert.setString(4, quote.category());
                insert.executeUpdate();
            }
        }
    }
}

