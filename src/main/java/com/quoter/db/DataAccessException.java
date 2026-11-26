package com.quoter.db;

/**
 * Wraps {@link java.sql.SQLException} so the UI layer can handle database
 * failures without depending on JDBC types.
 */
public class DataAccessException extends RuntimeException {

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
