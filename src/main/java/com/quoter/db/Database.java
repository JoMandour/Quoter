package com.quoter.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Owns the SQLite file location and hands out connections. Keeping the path in
 * an instance (instead of a static initializer) makes the data layer testable
 * against a temporary database.
 */
public final class Database {

    private final Path file;

    public Database(Path file) {
        this.file = Objects.requireNonNull(file, "file").toAbsolutePath();
    }

    public static Database defaultDatabase() {
        Path appDir = Paths.get(System.getProperty("user.home"), ".quoter");
        return new Database(appDir.resolve("Quotes.db"));
    }

    public Path file() {
        return file;
    }

    @FunctionalInterface
    public interface SqlWork<T> {
        T run(Connection connection) throws SQLException;
    }

    public <T> T withConnection(String action, SqlWork<T> work) {
        createParentDirectory();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + file)) {
            return work.run(connection);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to " + action, e);
        }
    }

    private void createParentDirectory() {
        Path parent = file.getParent();
        if (parent == null) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new DataAccessException("Failed to create database directory " + parent, e);
        }
    }
}
