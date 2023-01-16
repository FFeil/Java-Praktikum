package de.feil.view.dialog;

import javafx.scene.control.ChoiceDialog;

import java.util.List;

public class ChooseSettingDialog extends ChoiceDialog<String> {

    public ChooseSettingDialog(List<String> settings, String header) {
        super(settings.get(0), settings);

        setTitle("Einstellungen");
        setHeaderText(header);
        setContentText("Wähle eine Einstellung:");
    }
}
