package com.quoter.ui;

import com.quoter.AppContext;
import com.quoter.dao.QuoteDao;
import com.quoter.db.DataAccessException;
import com.quoter.model.Quote;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditorController {

    private final QuoteDao quoteDao = AppContext.quotes();

    @FXML private TextArea quoteInput;
    @FXML private TextField bookInput;
    @FXML private TextField authorInput;
    @FXML private ComboBox<String> categoryInput;

    private Runnable onCloseCallback;

    @FXML
    public void initialize() {
        categoryInput.setEditable(true);
        try {
            categoryInput.getItems().setAll(quoteDao.distinctCategories());
        } catch (DataAccessException e) {
            Dialogs.showError("Categories error", "Could not load categories.", e);
        }
    }

    public void setOnCloseCallback(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
    }

    @FXML
    private void handleSave() {
        String quoteText = trimmed(quoteInput.getText());
        if (quoteText.isEmpty()) {
            quoteInput.requestFocus();
            return;
        }

        String bookText = defaultIfBlank(trimmed(bookInput.getText()), Quote.UNKNOWN_BOOK);
        String authorText = defaultIfBlank(trimmed(authorInput.getText()), Quote.UNKNOWN_AUTHOR);
        String categoryText = defaultIfBlank(trimmed(categoryInput.getEditor().getText()), Quote.DEFAULT_CATEGORY);

        try {
            quoteDao.insert(new Quote(bookText, quoteText, authorText, categoryText));
            triggerClose();
        } catch (DataAccessException e) {
            Dialogs.showError("Save failed", "Could not save the quote.", e);
        }
    }

    @FXML
    private void handleCancel() {
        triggerClose();
    }

    private void triggerClose() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }

    private static String trimmed(String value) {
        return value == null ? "" : value.trim();
    }

    private static String defaultIfBlank(String value, String fallback) {
        return value.isEmpty() ? fallback : value;
    }
}
