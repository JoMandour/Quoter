package com.quoter.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.quoter.db.DataAccessException;
import com.quoter.db.Database;
import com.quoter.model.Quote;

class QuoteDaoTest {

    @TempDir
    Path tempDir;

    private Database database;
    private QuoteDao dao;

    @BeforeEach
    void setUp() {
        database = new Database(tempDir.resolve("quotes.db"));
        dao = new QuoteDao(database);
        dao.createTable();
    }

    @Test
    void createTableCanRunMoreThanOnce() {
        dao.createTable();
        dao.createTable();

        assertTrue(dao.getAll().isEmpty());
    }

    @Test
    void insertThenGetAllReturnsStoredQuote() {
        dao.insert(new Quote("Dune", "Fear is the mind-killer.", "Frank Herbert", "Sci-Fi"));

        List<Quote> quotes = dao.getAll();

        assertEquals(1, quotes.size());
        Quote saved = quotes.get(0);
        assertTrue(saved.id() > 0);
        assertEquals("Dune", saved.book());
        assertEquals("Fear is the mind-killer.", saved.quote());
        assertEquals("Frank Herbert", saved.author());
        assertEquals("Sci-Fi", saved.category());
    }

    @Test
    void getAllReturnsNewestQuoteFirst() {
        dao.insert(new Quote("First", "First quote", "Author A", "General"));
        dao.insert(new Quote("Second", "Second quote", "Author B", "General"));

        List<Quote> quotes = dao.getAll();

        assertEquals("Second", quotes.get(0).book());
        assertEquals("First", quotes.get(1).book());
    }

    @Test
    void distinctCategoriesAreSortedAndIgnoreBlankValues() {
        dao.insert(new Quote("Book A", "Quote A", "Author A", "Poetry"));
        dao.insert(new Quote("Book B", "Quote B", "Author B", "Fiction"));
        dao.insert(new Quote("Book C", "Quote C", "Author C", "Poetry"));
        dao.insert(new Quote("Book D", "Quote D", "Author D", "   "));

        assertEquals(List.of("Fiction", "Poetry"), dao.distinctCategories());
    }

    @Test
    void searchMatchesBookQuoteAuthorAndCategory() {
        dao.insert(new Quote("Dune", "Fear is the mind-killer.", "Frank Herbert", "Sci-Fi"));

        assertEquals(1, dao.search("dune").size());
        assertEquals(1, dao.search("mind-killer").size());
        assertEquals(1, dao.search("herbert").size());
        assertEquals(1, dao.search("sci-fi").size());
        assertTrue(dao.search("tolkien").isEmpty());
    }

    @Test
    void updateChangesEveryField() {
        dao.insert(new Quote("Old Book", "Old quote", "Old Author", "Old Category"));
        Quote stored = dao.getAll().get(0);

        boolean updated = dao.update(new Quote(stored.id(), "New Book", "New quote", "New Author", "New Category"));

        assertTrue(updated);
        Quote reloaded = dao.getAll().get(0);
        assertEquals("New Book", reloaded.book());
        assertEquals("New quote", reloaded.quote());
        assertEquals("New Author", reloaded.author());
        assertEquals("New Category", reloaded.category());
    }

    @Test
    void deleteRemovesQuoteAndReportsMissingRows() {
        dao.insert(new Quote("Dune", "Fear is the mind-killer.", "Frank Herbert", "Sci-Fi"));
        Quote stored = dao.getAll().get(0);

        assertTrue(dao.delete(stored.id()));
        assertTrue(dao.getAll().isEmpty());
        assertFalse(dao.delete(stored.id()));
    }

    @Test
    void migratesLegacyBookshelfTable() throws Exception {
        Path legacyFile = tempDir.resolve("legacy.db");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + legacyFile.toAbsolutePath());
             Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE Bookshelf (
                    id INTEGER PRIMARY KEY,
                    Book TEXT NOT NULL,
                    Quote TEXT NOT NULL,
                    Author TEXT NOT NULL,
                    Category TEXT NOT NULL
                )
                """);
            statement.execute("""
                INSERT INTO Bookshelf (Book, Quote, Author, Category)
                VALUES ('Legacy Book', 'Legacy quote', 'Legacy Author', 'Legacy Category')
                """);
        }

        QuoteDao legacyDao = new QuoteDao(new Database(legacyFile));
        legacyDao.createTable();

        List<Quote> quotes = legacyDao.getAll();
        assertEquals(1, quotes.size());
        assertEquals("Legacy Book", quotes.get(0).book());
    }

    @Test
    void databaseFailuresAreWrappedInDataAccessException() {
        // A directory cannot be opened as a SQLite database.
        QuoteDao brokenDao = new QuoteDao(new Database(tempDir));

        assertThrows(DataAccessException.class, brokenDao::getAll);
    }
}
