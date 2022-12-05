package de.feil.controller.editor;

import de.feil.view.dialog.ErrorAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

public class EditorController {

    private final String name;
    private final Stage editorStage;
    @FXML
    private TextArea codeTextArea;

    public EditorController(String name, Stage editorStage) {
        this.name = name;
        this.editorStage = editorStage;

        editorStage.setOnCloseRequest(this::onCloseRequest);
    }

    public String getName() {
        return name;
    }

    public void showStage(String code) {
        codeTextArea.setText(code);
        editorStage.show();
        editorStage.toFront();
    }

    private void onCloseRequest(WindowEvent event) {
        try {
            event.consume();
            String textAreaCode = codeTextArea.getText();
            textAreaCode = textAreaCode.replace("\n", System.lineSeparator());
            ArrayList<String> textAreaLines = new ArrayList<>();
            textAreaLines.add(textAreaCode);
            List<String> realLines = Files.readAllLines(Paths.get("automata/" + name + ".java"),
                    StandardCharsets.UTF_8);

            if (!textAreaLines.equals(realLines)) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Du hast noch nicht gespeichert!");
                    if (alert.showAndWait().filter(buttonType -> buttonType == ButtonType.OK).isPresent()) {
                        editorStage.close();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onSaveAction() {
        String code = codeTextArea.getText();
        code = code.replace("\n", System.lineSeparator());
        ArrayList<String> lines = new ArrayList<>();
        lines.add(code);

        // TODO: Funktioniert nicht
        try {
            File file = new File("automata", name + ".java");

            Files.write(Path.of(file.getPath()), lines, StandardCharsets.UTF_8);
            ToolProvider.getSystemJavaCompiler().run(null, null, null, file.getAbsolutePath());
        } catch (Exception e) {
            ErrorAlert.show("Ups, da ist was schief gelaufen:\n" + e);
        }
    }

    @FXML
    public void onCompileAction() {
        //TODO
    }

    @FXML
    public void onExitAction() {
        editorStage.close();
    }
}
