package de.feil;

import de.feil.model.base.Automaton;
import de.feil.util.FileHelper;
import de.feil.view.dialog.AlertHelper;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

public class Main extends Application {

    // VM Args: --module-path "C:\Program Files\Java\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // automata Ordner überprüfen/erstellen
        try {
            Path automataPath = Path.of("automata");
            if (!Files.exists(automataPath)) {
                Files.createDirectories(automataPath);
            }
        } catch (Exception e) { // .jar in .zip
            AlertHelper.showError("Beim Erstellen des Automata Verzeichnis ist ein Fehler aufgetreten. " +
                    "Vielleicht ist die .jar-Datei noch in einem .zip-Ordner:\n" + e);

            return;
        }

        // Initialen Automaten erstellen/laden
        final String initialAutomaton = "DefaultAutomaton";
        FileHelper.createFile(initialAutomaton);
        Optional<Automaton> optionalAutomaton = FileHelper.loadAutomaton(initialAutomaton, false);

        int ctr = 1;
        String newName = initialAutomaton;

        if (optionalAutomaton.isEmpty()) { // DefaultAutomaton kann nicht geladen werden
            AlertHelper.showError(newName, "Beim laden des Standard-Automaten ist ein Fehler augetreten! " +
                    "Eine Neuer wird nun erstellt.");

            while (optionalAutomaton.isEmpty()) { //DefaultAutomaton + i erstellen, i aus [ , 1, 2, ...]
                ctr++;
                newName = initialAutomaton + ctr;
                FileHelper.createFile(newName);
                optionalAutomaton = FileHelper.loadAutomaton(newName, false);
            }
        }

        // MVC-Set erstellen
        String finalNewName = newName;
        optionalAutomaton.ifPresent(automaton -> MVCSetCreator.create(finalNewName, new ArrayList<>(), automaton));
    }
}