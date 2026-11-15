import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;

public class SqliteAutoCreateCheck {
    public static void main(String[] args) throws Exception {
        Path database = Path.of("sqlite-auto-create-check.db");
        if (Files.exists(database)) {
            throw new IllegalStateException("Remove " + database + " before running this check");
        }

        try (var connection = DriverManager.getConnection("jdbc:sqlite:" + database)) {
        }

        System.out.println("Database automatically created: " + Files.exists(database));
    }
}
