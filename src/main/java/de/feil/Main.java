package de.feil;

import de.feil.util.FileHelper;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // VM Args: --module-path "C:\Program Files\Java\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final String initialAutomaton = "DefaultAutomaton";

        FileHelper.createFile(initialAutomaton);
        FileHelper.loadAutomaton(initialAutomaton)
                .ifPresent(automaton -> MVCSetCreator.create(initialAutomaton, automaton));
    }
}