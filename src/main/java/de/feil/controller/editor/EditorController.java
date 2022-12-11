package de.feil.controller.editor;

import de.feil.view.dialog.ErrorAlert;
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

            ArrayList<String> textAreaLines = new ArrayList<>(Arrays.asList(codeTextArea.getText().split("\n")));
            List<String> realLines = Files.readAllLines(Paths.get("automata/" + name + ".java"),
                    StandardCharsets.UTF_8);
            realLines.remove(realLines.size() - 1);

            if (textAreaLines.equals(realLines)) {
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
            e.printStackTrace();
        }
    }

    @FXML
    public void onSaveAction() {
        String code = codeTextArea.getText();
        code = code.replace("\n", System.lineSeparator());
        ArrayList<String> lines = new ArrayList<>();
        lines.add(code);

        try {
            File file = new File("automata", name + ".java");
            Files.write(Path.of(file.getPath()), lines, StandardCharsets.UTF_8);
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
