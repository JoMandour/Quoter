import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
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

        card.getChildren().addAll(quoteLabel, attributionLabel);
        return card;
    }
}