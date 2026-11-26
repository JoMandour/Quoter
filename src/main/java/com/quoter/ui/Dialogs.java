package com.quoter.ui;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/** Logs an error and shows it to the user in a modal dialog. */
public final class Dialogs {

    private static final Logger LOG = Logger.getLogger(Dialogs.class.getName());

    private Dialogs() {
    }

    public static void showError(String title, String message, Throwable error) {
        LOG.log(Level.SEVERE, message, error);
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
