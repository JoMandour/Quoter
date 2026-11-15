import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class QuoteDOA {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:sqlite:Quotes.db";
        try (Connection conn = DriverManager.getConnection(jdbcUrl)) {
            System.out.println("Connected to SQLite database: " + jdbcUrl);
        } catch (SQLException e) {
            System.err.println("Error connecting to SQLite");
            e.printStackTrace();
        }
    }
}