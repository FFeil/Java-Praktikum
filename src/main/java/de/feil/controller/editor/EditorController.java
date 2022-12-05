package de.feil.controller.editor;

import javafx.stage.Stage;

public class EditorController {

    private final Stage editorStage;

    public EditorController(Stage editorStage) {
        this.editorStage = editorStage;
    }

    public Stage getEditorStage() {
        return editorStage;
    }
}
