import java.util.ArrayList;
import java.util.List;

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

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private Pane quotesContainer;

    private List<Quote> allQuotes = new ArrayList<>();

    @FXML
    public void initialize() {
        allQuotes = QuoteDOA.getAllQuotes();

        categoryFilter.getItems().clear();
        categoryFilter.getItems().add(ALL_CATEGORIES);
        categoryFilter.getItems().addAll(QuoteDOA.getDistinctCategories());
        categoryFilter.setValue(ALL_CATEGORIES);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        renderQuotes(allQuotes);
    }

    private void applyFilters() {
        String query = searchField.getText() != null ? searchField.getText().trim().toLowerCase() : "";
        String selectedCategory = categoryFilter.getValue();

        List<Quote> filtered = allQuotes.stream()
            .filter(q -> {
                boolean matchesCategory = selectedCategory == null
                        || ALL_CATEGORIES.equals(selectedCategory)
                        || q.category().equalsIgnoreCase(selectedCategory);

                boolean matchesQuery = query.isEmpty()
                        || q.quote().toLowerCase().contains(query)
                        || q.author().toLowerCase().contains(query)
                        || q.book().toLowerCase().contains(query)
                        || q.category().toLowerCase().contains(query);

                return matchesCategory && matchesQuery;
            })
            .toList();

        renderQuotes(filtered);
    }

    private void renderQuotes(List<Quote> quotes) {
        quotesContainer.getChildren().clear();
        for (Quote q : quotes) {
            quotesContainer.getChildren().add(createQuoteCard(q));
        }
    }

    private VBox createQuoteCard(Quote q) {
        VBox card = new VBox(10);
        card.getStyleClass().add("quote-card");
        card.setPrefWidth(290);
        card.setMaxWidth(290);
        card.setPadding(new Insets(16));

        Label quoteLabel = new Label("“" + q.quote() + "”");
        quoteLabel.getStyleClass().add("quote-card-text");
        quoteLabel.setWrapText(true);

        Label attributionLabel = new Label("— " + q.author() + ", " + q.book());
        attributionLabel.getStyleClass().add("quote-card-meta");
        attributionLabel.setWrapText(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button deleteBtn = new Button("×");
        deleteBtn.getStyleClass().add("delete-btn");
        deleteBtn.setOnAction(e -> handleDelete(q));

        HBox footer = new HBox(8, attributionLabel, spacer, deleteBtn);
        footer.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(quoteLabel, footer);
        return card;
    }

    private void handleDelete(Quote q) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this quote permanently?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        confirm.setTitle("Delete Quote");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES && QuoteDOA.deleteQuote(q.id())) {
                allQuotes.removeIf(item -> item.id() == q.id());
                applyFilters();
            }
        }
     );
    }
    
}