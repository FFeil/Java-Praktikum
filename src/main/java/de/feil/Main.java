package de.feil;

import de.feil.model.base.Automaton;
import de.feil.model.implementation.KruemelmonsterAutomaton;
import de.feil.view.stage.MainStage;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    // VM Args: --module-path "C:\Program Files\Java\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Automaton automaton = new KruemelmonsterAutomaton(30, 30, 6, true);
        automaton.randomPopulation();

        new MainStage("KruemelmonsterAutomaton", automaton).show();
    }

    @Override
    public void stop() {
        for (File f : Objects.requireNonNull(new File("automata").listFiles())) {
            if (f.getName().endsWith(".class")) {
                f.delete();
            }
        }
    }
}