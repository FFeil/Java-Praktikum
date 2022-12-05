package de.feil;

import de.feil.util.FileLoader;
import de.feil.util.MVCSetCreator;
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
    public void start(Stage stage) {
        FileLoader.loadAutomaton("KruemelmonsterAutomaton",
                new File("automata/KruemelmonsterAutomaton.java")).ifPresent(automaton -> {
            automaton.randomPopulation();
            try {
                new MVCSetCreator("KruemelmonsterAutomaton", automaton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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