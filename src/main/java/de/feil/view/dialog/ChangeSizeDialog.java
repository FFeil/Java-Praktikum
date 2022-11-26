package de.feil.view.dialog;

import de.feil.util.Pair;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


import java.util.regex.Pattern;

public class ChangeSizeDialog extends Dialog<Pair<Integer>> {

    public ChangeSizeDialog(int numberOfRows, int numberOfCols) {
        setTitle("Größe ändern");
        setHeaderText("Welche Größe soll der Automat haben?" +
                "\n 5 <= Anzahl < =500");

        getDialogPane().getButtonTypes().add(ButtonType.OK);

        // Grid mit TextFields und Labels erstellen
        TextField rows = new TextField();
        TextField columns = new TextField();
        rows.setText("" + numberOfRows);
        columns.setText("" + numberOfCols);
        GridPane grid = new GridPane();
        getDialogPane().setContent(grid);

        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Zeilen:"), 0, 0);
        grid.add(new Label("Spalten:"), 0, 1);
        grid.add(columns, 1, 1);
        grid.add(rows, 1, 0);

        // Falls Werte falsch -> OK-Button disable
        BooleanBinding rowsBinding = Bindings.createBooleanBinding(
                () -> Pattern.matches("[5-9]|[1-9]\\d|[1-4]\\d\\d|500", rows.getText()), rows.textProperty());
        BooleanBinding columnsBinding = Bindings.createBooleanBinding(
                () -> Pattern.matches("[5-9]|[1-9]\\d|[1-4]\\d\\d|500", columns.getText()), columns.textProperty());

        getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(rowsBinding.not().or(columnsBinding.not()));

        // Ergebnis als Pair<Integer, Integer> zurückgeben
        setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new Pair<>(Integer.parseInt(rows.getText()), Integer.parseInt(columns.getText()));
            }
            return null;
        });
    }
}
