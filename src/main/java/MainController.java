import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private StackPane pageContainer;

    private String activeView = "";

    @FXML
    public void initialize() {
        loadLibraryView();
    }

    @FXML
    private void handleNewEntry() {
        if ("editor".equals(activeView)) return;
        loadEditorView();
    }

    @FXML
    private void handleLibrary() {
        if ("library".equals(activeView)) return;
        loadLibraryView();
    }

    private void loadLibraryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library.fxml"));
            Parent libraryNode = loader.load();
            activeView = "library";
            pageContainer.getChildren().setAll(libraryNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEditorView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editor_view.fxml"));
            Parent editorNode = loader.load();

            EditorController editorController = loader.getController();
            editorController.setOnCloseCallback(this::loadLibraryView);

            activeView = "editor";
            pageContainer.getChildren().setAll(editorNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}