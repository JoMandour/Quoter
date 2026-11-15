import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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
}