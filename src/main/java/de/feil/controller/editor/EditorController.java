package de.feil.controller.editor;

import de.feil.controller.references.ReferenceHandler;
import de.feil.util.FileHelper;
import de.feil.view.dialog.AlertHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditorController {

    private final ReferenceHandler referenceHandler;
    private final Stage editorStage;

    @FXML
    private TextArea codeTextArea;

    public EditorController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;
        this.editorStage = referenceHandler.getEditorStage();

        referenceHandler.setEditorController(this);
        editorStage.setOnCloseRequest(this::onCloseRequest);
    }

    public void showStage(String code) {
        codeTextArea.setText(code);
        editorStage.show();
        editorStage.toFront();
    }

    private void onCloseRequest(WindowEvent event) {
        try {
            event.consume();

            List<String> newLines = new ArrayList<>(Arrays.asList(codeTextArea.getText().split("\n")));
            List<String> oldLines = Files.readAllLines(Paths.get("automata/" + referenceHandler.getName() + ".java"),
                    StandardCharsets.UTF_8);

            if (newLines.equals(oldLines)) {
                editorStage.close();
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Bist du sicher? Du hast noch nicht gespeichert!");
                    if (alert.showAndWait().filter(buttonType -> buttonType == ButtonType.OK).isPresent()) {
                        editorStage.close();
                    }
                });
            }
        } catch (IOException e) {
            AlertHelper.showError("Beim Schließen des Editors ist ein Fehler aufgetreten:\n" + e);
        }
    }

    @FXML
    public void onSaveAction() {
        String code = codeTextArea.getText();
        code = code.replace("\n", System.lineSeparator());
        ArrayList<String> lines = new ArrayList<>();
        lines.add(code);

        try {
            File file = new File("automata", referenceHandler.getName() + ".java");
            Files.write(Path.of(file.getPath()), lines, StandardCharsets.UTF_8);
        } catch (Exception e) {
            AlertHelper.showError("Beim Speichern des Codes ist ein Fehler aufgetreten:\n" + e);
        }
    }

    @FXML
    public void onCompileAction() {
        onSaveAction();

        FileHelper.loadAutomaton(referenceHandler.getName()).ifPresent(automaton -> {
            referenceHandler.setAutomaton(automaton);
            AlertHelper.showInformation("Kompilieren erfolgreich!");
        });
    }

    @FXML
    public void onExitAction() {
        editorStage.close();
    }
}
