import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuoteDOA {

    private static final String JDBC_URL = initDatabaseUrl();

    private static String initDatabaseUrl() {
        try {
            Path appDir = Paths.get(System.getProperty("user.home"), ".quoter");
            Files.createDirectories(appDir);
            return "jdbc:sqlite:" + appDir.resolve("Quotes.db").toAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "jdbc:sqlite:Quotes.db";
        }
    }

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

    public static List<Quote> getAllQuotes() {
        List<Quote> results = new ArrayList<>();
        String sql = "SELECT * FROM Bookshelf ORDER BY id DESC;";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                results.add(new Quote(
                    rs.getInt("id"),
                    rs.getString("Book"),
                    rs.getString("Quote"),
                    rs.getString("Author"),
                    rs.getString("Category")
                ));
            }
        } catch (SQLException e) {
            IO.println("Error fetching all quotes.");
            e.printStackTrace();
        }
        return results;
    }

    public static List<String> getDistinctCategories() {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT Category FROM Bookshelf WHERE TRIM(Category) != '' ORDER BY Category ASC;";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
        } catch (SQLException e) {
            IO.println("Error fetching distinct categories.");
            e.printStackTrace();
        }
        return categories;
    }

    public static List<Quote> searchQuotes(String keyword) {
        List<Quote> results = new ArrayList<>();
        String sql = "SELECT * FROM Bookshelf WHERE Book LIKE ? OR Quote LIKE ? OR Author LIKE ? OR Category LIKE ?;";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            pstmt.setString(3, "%" + keyword + "%");
            pstmt.setString(4, "%" + keyword + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new Quote(
                    rs.getInt("id"),
                    rs.getString("Book"),
                    rs.getString("Quote"),
                    rs.getString("Author"),
                    rs.getString("Category")
                ));
            }
        } catch (SQLException e) {
            IO.println("Err Searching in DB");
            e.printStackTrace();
        }
        return results;
    }

    public static boolean deleteQuote(int id) {
        String sql = "DELETE FROM Bookshelf WHERE id = ?;";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            IO.println("Err deleting the quote.");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updateQuote(Quote q) {
        String sql = "UPDATE Bookshelf SET Book = ?, Quote = ?, Author = ?, Category = ? WHERE id = ?;";

        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, q.book());
            pstmt.setString(2, q.quote());
            pstmt.setString(3, q.author());
            pstmt.setString(4, q.category());
            pstmt.setInt(5, q.id());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            IO.println("Err updating quote");
            e.printStackTrace();
        }
        return false;
    }
}