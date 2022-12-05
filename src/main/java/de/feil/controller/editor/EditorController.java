package de.feil.controller.editor;

import de.feil.view.dialog.ErrorAlert;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class EditorController {

    private final String fileName;
    private final Stage editorStage;
    @FXML
    private TextArea codeTextArea;

    public EditorController(String fileName, Stage editorStage) {
        this.fileName = fileName;
        this.editorStage = editorStage;
    }

    public void showStage(String code) {
        codeTextArea.setText(code);
        editorStage.show();
        editorStage.toFront();
    }

    @FXML
    public void onSaveAction() throws IOException {
        String code = codeTextArea.getText();
        code = code.replace("\n", System.lineSeparator());
        ArrayList<String> lines = new ArrayList<>();
        lines.add(code);

        try {
            File file = new File("automata", fileName + ".java");

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
