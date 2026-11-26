package com.quoter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.quoter.db.Database;
import com.quoter.model.Quote;

public final class QuoteDao {

    private static final Logger LOG = Logger.getLogger(QuoteDao.class.getName());

    private static final String SELECT_COLUMNS = "id, book, quote, author, category";

    private final Database database;

    public QuoteDao(Database database) {
        this.database = Objects.requireNonNull(database, "database");
    }

    public void createTable() {
        database.withConnection("create the quotes table", connection -> {
            try (Statement statement = connection.createStatement()) {
                statement.execute("""
                    CREATE TABLE IF NOT EXISTS quotes (
                        id INTEGER PRIMARY KEY,
                        book TEXT NOT NULL,
                        quote TEXT NOT NULL,
                        author TEXT NOT NULL,
                        category TEXT NOT NULL
                    )
                    """);
            }
            migrateLegacyBookshelf(connection);
            return null;
        });
        LOG.info("Quotes table is ready.");
    }

    public void insert(Quote quote) {
        database.withConnection("insert a quote", connection -> {
            String sql = "INSERT INTO quotes (book, quote, author, category) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindQuote(statement, quote, 1);
                int rows = statement.executeUpdate();
                LOG.info(() -> "Inserted " + rows + " quote row(s).");
            }
            return null;
        });
    }

    public List<Quote> getAll() {
        return database.withConnection("fetch all quotes", connection -> {
            String sql = "SELECT " + SELECT_COLUMNS + " FROM quotes ORDER BY id DESC";
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                return mapAll(resultSet);
            }
        });
    }

    public List<String> distinctCategories() {
        return database.withConnection("fetch distinct categories", connection -> {
            String sql = "SELECT DISTINCT category FROM quotes "
                    + "WHERE TRIM(category) != '' ORDER BY category ASC";
            List<String> categories = new ArrayList<>();
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                while (resultSet.next()) {
                    categories.add(resultSet.getString("category"));
                }
            }
            return categories;
        });
    }

    public List<Quote> search(String keyword) {
        return database.withConnection("search quotes", connection -> {
            String sql = "SELECT " + SELECT_COLUMNS + " FROM quotes "
                    + "WHERE book LIKE ? OR quote LIKE ? OR author LIKE ? OR category LIKE ? "
                    + "ORDER BY id DESC";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                String pattern = "%" + keyword + "%";
                for (int index = 1; index <= 4; index++) {
                    statement.setString(index, pattern);
                }
                try (ResultSet resultSet = statement.executeQuery()) {
                    return mapAll(resultSet);
                }
            }
        });
    }

    public boolean delete(int id) {
        return database.withConnection("delete a quote", connection -> {
            String sql = "DELETE FROM quotes WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                return statement.executeUpdate() > 0;
            }
        });
    }

    public boolean update(Quote quote) {
        return database.withConnection("update a quote", connection -> {
            String sql = "UPDATE quotes SET book = ?, quote = ?, author = ?, category = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                bindQuote(statement, quote, 1);
                statement.setInt(5, quote.id());
                return statement.executeUpdate() > 0;
            }
        });
    }

    private static void bindQuote(PreparedStatement statement, Quote quote, int startIndex) throws SQLException {
        statement.setString(startIndex, quote.book());
        statement.setString(startIndex + 1, quote.quote());
        statement.setString(startIndex + 2, quote.author());
        statement.setString(startIndex + 3, quote.category());
    }

    private static List<Quote> mapAll(ResultSet resultSet) throws SQLException {
        List<Quote> quotes = new ArrayList<>();
        while (resultSet.next()) {
            quotes.add(mapRow(resultSet));
        }
        return quotes;
    }

    private static Quote mapRow(ResultSet resultSet) throws SQLException {
        return new Quote(
                resultSet.getInt("id"),
                resultSet.getString("book"),
                resultSet.getString("quote"),
                resultSet.getString("author"),
                resultSet.getString("category"));
    }

    /**
     * Earlier versions used a {@code Bookshelf} table with PascalCase columns.
     * Move any existing rows into the new {@code quotes} table so upgrades do
     * not lose data.
     */
    private static void migrateLegacyBookshelf(Connection connection) throws SQLException {
        if (!tableExists(connection, "Bookshelf")) {
            return;
        }

        LOG.info("Migrating the legacy Bookshelf table into quotes.");
        boolean autoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("""
                    INSERT OR IGNORE INTO quotes (id, book, quote, author, category)
                    SELECT id, Book, Quote, Author, Category FROM Bookshelf
                    """);
                statement.execute("DROP TABLE Bookshelf");
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(autoCommit);
        }
    }

    private static boolean tableExists(Connection connection, String name) throws SQLException {
        String sql = "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
