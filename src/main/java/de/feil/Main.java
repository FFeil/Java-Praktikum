package de.feil;

import de.feil.model.base.Automaton;
import de.feil.util.FileHelper;
import de.feil.view.dialog.AlertHelper;
import javafx.application.Application;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main extends Application {

    private static final String INITIAL_AUTOMATON = "DefaultAutomaton";

    // VM Args: --module-path "C:\Program Files\Java\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        if (!validateDirectories()) {
            return;
        }

        loadInitialAutomaton();
    }

    private static boolean validateDirectories() {
        try {
            // automata-Verzeichnis
            Path automataPath = Path.of("automata");
            if (!Files.exists(automataPath)) {
                Files.createDirectories(automataPath);
            }

            // populations-Verzeichnis
            Path populationsPath = Path.of("populations");
            if (!Files.exists(populationsPath)) {
                Files.createDirectories(populationsPath);
            }
        } catch (Exception e) { // Vermutlich: .jar in .zip
            AlertHelper.showError("Beim Validieren bzw. Erstellen der Ordner ist ein Fehler " +
                    "aufgetreten. Vielleicht ist die .jar-Datei noch in einem .zip-Ordner:\n" + e);

            return false;
        }
        return true;
    }


    private void loadInitialAutomaton() {
        FileHelper.createFile(INITIAL_AUTOMATON);
        Optional<Automaton> optionalAutomaton = FileHelper.loadAutomaton(INITIAL_AUTOMATON, false);
        String automatonToLoad = INITIAL_AUTOMATON;
        List<String> corruptedAutomata = new ArrayList<>();

        // INITIAL_AUTOMATON kann nicht geladen werden => <INITIAL_AUTOMATON + i> erstellen, i aus [ , 1, 2, ...]
        if (optionalAutomaton.isEmpty()) {
            AlertHelper.showError(automatonToLoad, "Beim laden des Standard-Automaten <" + INITIAL_AUTOMATON + "> " +
                    "ist ein Fehler aufgetreten! Ein Neuer wird nun geladen.");

            corruptedAutomata.add(INITIAL_AUTOMATON);
            int ctr = 2;

            while (optionalAutomaton.isEmpty()) {
                automatonToLoad = INITIAL_AUTOMATON + ctr;
                corruptedAutomata.add(automatonToLoad);

                FileHelper.createFile(automatonToLoad);
                optionalAutomaton = FileHelper.loadAutomaton(automatonToLoad, false);

                ctr++;
            }
        }

        // Fehlerhafte Automaten ausgeben
        if (!corruptedAutomata.isEmpty()) {
            corruptedAutomata.remove(corruptedAutomata.size() - 1); // Der Automat, der geladen wird
            AlertHelper.showInformation("<" + automatonToLoad + "> wurde erfolgreich geladen. Folgende Automaten " +
                    "konnten nicht kompiliert werden, da der Code fehlerhaft ist:\n" + corruptedAutomata);
        }

        // MVC-Set erstellen
        String finalAutomatonName = automatonToLoad;
        optionalAutomaton.ifPresent(automaton -> MVCSetCreator.create(finalAutomatonName, new ArrayList<>(), automaton));
    }
}