package com.quoter.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.quoter.AppContext;
import com.quoter.dao.QuoteDao;
import com.quoter.db.DataAccessException;
import com.quoter.model.Quote;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class LibraryController {

    private static final String ALL_CATEGORIES = "All Categories";
    private static final double CARD_WIDTH = 290;

    private final QuoteDao quoteDao = AppContext.quotes();

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Pane quotesContainer;

    private List<Quote> allQuotes = new ArrayList<>();

    @FXML
    public void initialize() {
        loadQuotes();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        renderQuotes(allQuotes);
    }

    private void loadQuotes() {
        categoryFilter.getItems().setAll(ALL_CATEGORIES);
        categoryFilter.setValue(ALL_CATEGORIES);

        try {
            allQuotes = quoteDao.getAll();
            categoryFilter.getItems().addAll(quoteDao.distinctCategories());
        } catch (DataAccessException e) {
            allQuotes = new ArrayList<>();
            Dialogs.showError("Library error", "Could not load your quotes.", e);
        }
    }

    private void applyFilters() {
        String query = searchField.getText() == null
                ? ""
                : searchField.getText().trim().toLowerCase(Locale.ROOT);
        String selectedCategory = categoryFilter.getValue();

        List<Quote> filtered = allQuotes.stream()
                .filter(quote -> matchesCategory(quote, selectedCategory))
                .filter(quote -> matchesQuery(quote, query))
                .toList();

        renderQuotes(filtered);
    }

    private static boolean matchesCategory(Quote quote, String selectedCategory) {
        return selectedCategory == null
                || ALL_CATEGORIES.equals(selectedCategory)
                || quote.category().equalsIgnoreCase(selectedCategory);
    }

    private static boolean matchesQuery(Quote quote, String query) {
        if (query.isEmpty()) {
            return true;
        }
        return quote.quote().toLowerCase(Locale.ROOT).contains(query)
                || quote.author().toLowerCase(Locale.ROOT).contains(query)
                || quote.book().toLowerCase(Locale.ROOT).contains(query)
                || quote.category().toLowerCase(Locale.ROOT).contains(query);
    }

    private void renderQuotes(List<Quote> quotes) {
        quotesContainer.getChildren().clear();
        for (Quote quote : quotes) {
            quotesContainer.getChildren().add(createQuoteCard(quote));
        }
    }

    private VBox createQuoteCard(Quote quote) {
        VBox card = new VBox(10);
        card.getStyleClass().add("quote-card");
        card.setPrefWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);
        card.setPadding(new Insets(16));

        Label quoteLabel = new Label("\u201C" + quote.quote() + "\u201D");
        quoteLabel.getStyleClass().add("quote-card-text");
        quoteLabel.setWrapText(true);

        Label attributionLabel = new Label("\u2014 " + quote.author() + ", " + quote.book());
        attributionLabel.getStyleClass().add("quote-card-meta");
        attributionLabel.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteButton = new Button("\u00D7");
        deleteButton.getStyleClass().add("delete-btn");
        deleteButton.setOnAction(event -> handleDelete(quote));

        HBox footer = new HBox(8, attributionLabel, spacer, deleteButton);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(quoteLabel, footer);
        return card;
    }

    private void handleDelete(Quote quote) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this quote permanently?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Delete Quote");

        confirm.showAndWait().ifPresent(response -> {
            if (response != ButtonType.YES) {
                return;
            }
            try {
                if (quoteDao.delete(quote.id())) {
                    allQuotes.removeIf(item -> item.id() == quote.id());
                    applyFilters();
                }
            } catch (DataAccessException e) {
                Dialogs.showError("Delete failed", "Could not delete the quote.", e);
            }
        });
    }
}
