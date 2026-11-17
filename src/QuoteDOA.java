import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
public class QuoteDOA {

    private static final String JDBC_URL = "jdbc:sqlite:Quotes.db";

    public static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Bookshelf (
                id INTEGER PRIMARY KEY,
                Book TEXT NOT NULL,
                Quote TEXT NOT NULL,
                Author TEXT NOT NULL,
                Category TEXT NOT NULL
            );
            """;

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            IO.println("Table ready.");
        } catch (SQLException e) {
            IO.println("Error connecting to DB.");
            e.printStackTrace();
        }
    }

    public static void insertData(Quote q) {
        String sql = "INSERT INTO Bookshelf (Book, Quote, Author, Category) VALUES (?, ?, ?, ?);";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, q.book());
            pstmt.setString(2, q.quote());
            pstmt.setString(3, q.author());
            pstmt.setString(4, q.category());

            int rowsAffected = pstmt.executeUpdate();
            IO.println("Inserted " + rowsAffected + " row(s) for \"" + q.quote() + "\".");
        } catch (SQLException e) {
            IO.println("Error inserting data.");
            e.printStackTrace();
        }
    }

    public static List<Quote> searchQuotes(String keyword) {
        List<Quote> results = new ArrayList<>();
        String sql = "SELECT * FROM Bookshelf WHERE Book LIKE ? OR Quote LIKE ? OR Author LIKE ? OR Category LIKE ?;";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            pstmt.setString(4, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {

                int id = rs.getInt("id");
                String book = rs.getString("Book");
                String quote = rs.getString("Quote");
                String author = rs.getString("Author");
                String category = rs.getString("Category");
                results.add(new Quote(id, book, quote, author, category));

            }
            
            
        } catch (SQLException e) {
            IO.println("Err Searching in DB");
            e.printStackTrace();
        }
        return results;
    }

    public static boolean deleteQuote (int id) {
        String sql = "DELETE FROM Bookshelf WHERE id = ?;";

        try(Connection conn = DriverManager.getConnection(JDBC_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
           ) {
            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

           } catch (SQLException e) {
            IO.println("Err deleting the quote.");
            e.printStackTrace();
           }
           return false;

    }

    public static boolean updateQuote(Quote q) {
        String sql = "UPDATE Bookshelf SET Book = ?, Quote = ?, Author = ?, Category = ? WHERE id = ?;";

        try(Connection conn = DriverManager.getConnection(JDBC_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql);
           ) {
            pstmt.setString(1, q.book());
            pstmt.setString(2, q.quote());
            pstmt.setString(3, q.author());
            pstmt.setString(4, q.category());
            pstmt.setInt(5, q.id());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
           } catch (SQLException e) {
            IO.println("Err updating quote");
            e.printStackTrace();
           }
           return false;


    }
}