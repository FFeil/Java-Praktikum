package de.feil.view.dialog;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.List;
import java.util.regex.Pattern;

public class SaveSettingsDialog extends TextInputDialog {

    public SaveSettingsDialog(List<String> settings) {
        setTitle("Einstellungen");
        setHeaderText("Unter welchen Namen sollen die Einstellungen\ngespeichert werden? Du darfst höchstens\n40 Zeichen benutzen!");
        setContentText("Name:");

        BooleanBinding only40CharsBindings = Bindings.createBooleanBinding(
                () -> Pattern.matches("\\S{1,40}",
                        getEditor().getText()), getEditor().textProperty());
        BooleanBinding duplicateNameBinding = Bindings.createBooleanBinding(
                () -> settings.contains(getEditor().getText()), getEditor().textProperty());

        getDialogPane().lookupButton(ButtonType.OK).disableProperty()
                .bind(only40CharsBindings.not().or(duplicateNameBinding));
    }
}
