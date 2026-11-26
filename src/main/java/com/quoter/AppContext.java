package com.quoter;

import java.util.Objects;

import com.quoter.dao.QuoteDao;
import com.quoter.db.Database;

/**
 * Minimal service locator. JavaFX instantiates FXML controllers itself, so the
 * controllers pull their dependencies from here instead of receiving them
 * through constructors.
 */
public final class AppContext {

    private static QuoteDao quoteDao = new QuoteDao(Database.defaultDatabase());

    private AppContext() {
    }

    public static QuoteDao quotes() {
        return quoteDao;
    }

    public static void setQuoteDao(QuoteDao dao) {
        quoteDao = Objects.requireNonNull(dao, "dao");
    }
}
