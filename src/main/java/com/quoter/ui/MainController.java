package com.quoter.ui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class MainController {

    private enum View {
        LIBRARY,
        EDITOR
    }

    @FXML private StackPane pageContainer;

    private View activeView;

    @FXML
    public void initialize() {
        loadLibraryView();
    }

    @FXML
    private void handleNewEntry() {
        if (activeView == View.EDITOR) {
            return;
        }
        loadEditorView();
    }

    @FXML
    private void handleLibrary() {
        if (activeView == View.LIBRARY) {
            return;
        }
        loadLibraryView();
    }

    private void loadLibraryView() {
        Parent libraryNode = load("/library.fxml");
        activeView = View.LIBRARY;
        pageContainer.getChildren().setAll(libraryNode);
    }

    private void loadEditorView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/editor_view.fxml"));
        Parent editorNode = load(loader);

        if (loader.getController() instanceof EditorController editorController) {
            editorController.setOnCloseCallback(this::loadLibraryView);
        }

        activeView = View.EDITOR;
        pageContainer.getChildren().setAll(editorNode);
    }

    private Parent load(String resource) {
        return load(new FXMLLoader(getClass().getResource(resource)));
    }

    private Parent load(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            Dialogs.showError("View error", "Could not load view " + loader.getLocation(), e);
            return new StackPane();
        }
    }
}
