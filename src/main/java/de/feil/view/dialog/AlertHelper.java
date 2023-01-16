package de.feil.view.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertHelper {

    private AlertHelper() {}

    public static void showError(String nameOfAutomaton, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setTitle(alert.getTitle() + ": " + nameOfAutomaton);
        alert.showAndWait();
    }

    public static void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }

    public static void showInformation(String nameOfAutomaton, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.setTitle(alert.getTitle() + ": " + nameOfAutomaton);
        alert.showAndWait();
    }
}
