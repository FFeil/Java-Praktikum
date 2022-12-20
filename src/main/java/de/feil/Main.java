package de.feil;

import de.feil.model.base.Automaton;
import de.feil.util.FileHelper;
import de.feil.view.dialog.AlertHelper;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Optional;

public class Main extends Application {

    // VM Args: --module-path "C:\Program Files\Java\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        final String initialAutomaton = "DefaultAutomaton";

        FileHelper.createFile(initialAutomaton);

        Optional<Automaton> optionalAutomaton = FileHelper.loadAutomaton(initialAutomaton, false);
        int ctr = 1;
        String newName = initialAutomaton;

        if (optionalAutomaton.isEmpty()) {
            AlertHelper.showError(newName, "Beim laden des Standard-Automaten ist ein Fehler augetreten! " +
                    "Eine Neuer wird nun erstellt.");

            while (optionalAutomaton.isEmpty()) {
                ctr++;
                newName = initialAutomaton + ctr;
                FileHelper.createFile(newName);
                optionalAutomaton = FileHelper.loadAutomaton(newName, false);
            }
        }

        String finalNewName = newName;
        optionalAutomaton.ifPresent(automaton -> MVCSetCreator.create(finalNewName, automaton));
    }
}