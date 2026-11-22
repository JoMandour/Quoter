import java.io.IOException;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class MainController {

    @FXML private StackPane pageContainer;

    private boolean isAnimating = false;
    private String activeView = "";

    @FXML
    public void initialize() {
        loadLibraryView(false);
    }

    @FXML
    private void handleNewEntry() {
        if (isAnimating || "editor".equals(activeView)) return;
        loadEditorView();
    }

    @FXML
    private void handleLibrary() {
        if (isAnimating || "library".equals(activeView)) return;
        loadLibraryView(true);
    }

    private void loadLibraryView(boolean animate) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library.fxml"));
            Parent libraryNode = loader.load();
            activeView = "library";

            if (!animate || pageContainer.getChildren().isEmpty()) {
                pageContainer.getChildren().setAll(libraryNode);
            } else {
                flipToView(libraryNode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEditorView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editor_view.fxml"));
            Parent editorNode = loader.load();

            EditorController editorController = loader.getController();
            editorController.setOnCloseCallback(() -> loadLibraryView(true));

            activeView = "editor";
            flipToView(editorNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void flipToView(Parent incomingNode) {
        if (pageContainer.getChildren().isEmpty()) {
            pageContainer.getChildren().setAll(incomingNode);
            return;
        }

        isAnimating = true;
        Node outgoingNode = pageContainer.getChildren().getLast();

        
        pageContainer.getChildren().addFirst(incomingNode);

        
        Rotate hingeRotate = new Rotate(0, 0, 0, 0, Rotate.Y_AXIS);
        outgoingNode.getTransforms().add(hingeRotate);

        Timeline pageTurn = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(hingeRotate.angleProperty(), 0),
                new KeyValue(outgoingNode.opacityProperty(), 1.0)
            ),
            new KeyFrame(Duration.millis(300),
                new KeyValue(hingeRotate.angleProperty(), -90, Interpolator.EASE_IN),
                new KeyValue(outgoingNode.opacityProperty(), 0.15, Interpolator.EASE_IN)
            )
        );

        pageTurn.setOnFinished(event -> {
            pageContainer.getChildren().remove(outgoingNode);
            isAnimating = false;
        });

        pageTurn.play();
    }
}