package de.feil.automaton;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.regex.Pattern;

public class ChangeSizeDialog extends Dialog<Pair<Integer, Integer>> {

    public ChangeSizeDialog() {
        setTitle("Größe ändern");
        setHeaderText("Welche Größe soll der Automat haben?" +
                "\n 5 <= Anzahl < =500");

        getDialogPane().getButtonTypes().add(ButtonType.OK);

        // Grid mit TextFields und Labels erstellen
        TextField rows = new TextField();
        TextField columns = new TextField();
        GridPane grid = new GridPane();
        getDialogPane().setContent(grid);

        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Zeilen:"), 0, 0);
        grid.add(new Label("Spalten:"), 0, 1);
        grid.add(columns, 1, 1);
        grid.add(rows, 1, 0);

        // Falls Werte falsch -> OK-Button unterdrücken
        setOnCloseRequest(event -> { // 4 < x > 501
            if (!Pattern.matches("[5-9]|[1-9]\\d|[1-4]\\d\\d|500", rows.getText())
                    || !Pattern.matches("[5-9]|[1-9]\\d|[1-4]\\d\\d|500", columns.getText())) {
                event.consume();
                setResult(null);
            }
        });

        // Ergebnis als Pair<Integer, Integer> zurückgeben
        setResultConverter(button -> {
            if (button == ButtonType.OK && !rows.getText().isEmpty() && !columns.getText().isEmpty()) {
                return new Pair<>(Integer.parseInt(rows.getText()), Integer.parseInt(columns.getText()));
            }
            return null;
        });
    }
}
