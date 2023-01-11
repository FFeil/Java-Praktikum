package de.feil.controller.serialization;

import de.feil.controller.references.ReferenceHandler;
import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;
import de.feil.view.dialog.AlertHelper;
import javafx.stage.FileChooser;

import java.io.*;

public class SerializationController {

    private static FileChooser fileChooser;

    static {
        fileChooser = new FileChooser();
        File dir = new File("populations");
        fileChooser.setInitialDirectory(dir);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.ser", "*.ser"));
    }

    public SerializationController(ReferenceHandler referenceHandler) {
        referenceHandler.getMainController().getSerializeMenuItem().setOnAction(e -> savePopulation(referenceHandler));
        referenceHandler.getMainController().getDeserializeMenuItem().setOnAction(e -> loadPopulation(referenceHandler));
    }

    public void savePopulation(ReferenceHandler referenceHandler) {
        Automaton automaton = referenceHandler.getAutomaton();
        fileChooser.setTitle("Speiche Serializierung");
        File file = fileChooser.showSaveDialog(referenceHandler.getMainStage());

        if (file == null) {
            return;
        }

        if (!file.getName().endsWith(".ser")) {
            file = new File(file.getAbsolutePath().concat(".ser"));
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            synchronized (automaton) {
                outputStream.writeInt(automaton.getNumberOfStates());
                outputStream.writeInt(automaton.getNumberOfRows());
                outputStream.writeInt(automaton.getNumberOfColumns());
                outputStream.writeObject(automaton.getCells());
            }
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Fehler beim Speichern:\n" + e);
        }
    }

    public void loadPopulation(ReferenceHandler referenceHandler) {
        Automaton automaton = referenceHandler.getAutomaton();
        fileChooser.setTitle("Lade Serializierung");
        File file = fileChooser.showOpenDialog(referenceHandler.getMainStage());

        if (file == null) {
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            if (inputStream.readInt() != automaton.getNumberOfStates()) {
                throw new Exception("Die Anzahl der Zustände stimmen nicht überein!");
            }

            int numberOfCols = inputStream.readInt();
            int numberOfRows = inputStream.readInt();
            Cell[][] cells = (Cell[][]) inputStream.readObject();

            automaton.changeSize(numberOfRows, numberOfCols);
            for (int i = 0; i < numberOfRows; i++) {
                for (int j = 0; j < numberOfCols; j++) {
                    automaton.setState(i, j, cells[i][j].getState());
                }
            }
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Fehler beim Laden:\n" + e);
        }
    }
}
