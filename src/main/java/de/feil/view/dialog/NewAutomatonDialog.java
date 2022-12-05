package de.feil.view.dialog;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.regex.Pattern;

public class NewAutomatonDialog extends TextInputDialog {

    public NewAutomatonDialog() {
        setTitle("Neuer Automat");
        setHeaderText("Wie soll der neue Automat heißen?\n" +
                "Du musst dich an die Namenskonventionen\n" +
                "von Java halten!");
        setContentText("Name:");

        BooleanBinding binding = Bindings.createBooleanBinding(
                () -> Pattern.matches("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*"
                        , getEditor().getText()), getEditor().textProperty());

        getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(binding.not());
    }
}
