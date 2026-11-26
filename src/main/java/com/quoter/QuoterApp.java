package com.quoter;

import com.quoter.db.DataAccessException;
import com.quoter.ui.Dialogs;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class QuoterApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        try {
            AppContext.quotes().createTable();
        } catch (DataAccessException e) {
            Dialogs.showError("Database error", "Could not initialise the quote database.", e);
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource("/main_shell.fxml"));
        Scene scene = new Scene(root, 950, 650);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("Quoter");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
