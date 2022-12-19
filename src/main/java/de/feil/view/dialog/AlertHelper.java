package de.feil.view.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertHelper {

    private AlertHelper() {}

    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public static void showInformation(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}
