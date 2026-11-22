import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditorController {

    @FXML private TextArea quoteInput;
    @FXML private TextField bookInput;
    @FXML private TextField authorInput;
    @FXML private ComboBox<String> categoryInput;

    private Runnable onCloseCallback;

    @FXML
    public void initialize() {
        categoryInput.setEditable(true);
        categoryInput.getItems().setAll(QuoteDOA.getDistinctCategories());
    }

    public void setOnCloseCallback(Runnable onCloseCallback) {
        this.onCloseCallback = onCloseCallback;
    }

    @FXML
    private void handleSave() {
        String quoteText = quoteInput.getText() != null ? quoteInput.getText().trim() : "";
        if (quoteText.isEmpty()) {
            quoteInput.requestFocus();
            return;
        }

        String bookText = bookInput.getText() != null ? bookInput.getText().trim() : "Unknown Book";
        String authorText = authorInput.getText() != null ? authorInput.getText().trim() : "Unknown Author";
        String categoryText = categoryInput.getEditor().getText() != null
                ? categoryInput.getEditor().getText().trim()
                : "";

        if (bookText.isEmpty()) bookText = "Unknown Book";
        if (authorText.isEmpty()) authorText = "Unknown Author";
        if (categoryText.isEmpty()) categoryText = "General";

        Quote newQuote = new Quote(bookText, quoteText, authorText, categoryText);
        QuoteDOA.insertData(newQuote);

        triggerClose();
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
}