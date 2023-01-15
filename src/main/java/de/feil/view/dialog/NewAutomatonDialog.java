package de.feil.view.dialog;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.util.regex.Pattern;

public class NewAutomatonDialog extends TextInputDialog {

    public NewAutomatonDialog() {
        setTitle("Neuer Automat");
        setHeaderText("Wie soll der neue Automat heißen?\nDu musst dich an die Namenskonventionen" +
                "\nvon Java halten, darfst Namen nicht doppelt\nverwenden und ihn nicht DefaultAutomaton\nnennen!");
        setContentText("Name:");

        BooleanBinding javaClassNameBinding = Bindings.createBooleanBinding(
                () -> Pattern.matches("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*",
                        getEditor().getText()), getEditor().textProperty());
        BooleanBinding defaultAutomatonBinding = Bindings.createBooleanBinding(
                () -> Pattern.matches("[DefaultAutomaton][\\d*]",
                        getEditor().getText()), getEditor().textProperty());
        BooleanBinding nameExistsBinding = Bindings.createBooleanBinding(
                () -> (new File("automata", getEditor().getText() + ".java").exists()),
                        getEditor().textProperty());

        getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(javaClassNameBinding.not().or(nameExistsBinding).or(defaultAutomatonBinding));
    }
}
