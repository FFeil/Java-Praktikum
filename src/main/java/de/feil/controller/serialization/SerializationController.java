package de.feil.controller.serialization;

import de.feil.controller.references.ReferenceHandler;
import de.feil.controller.serialization.exception.InvalidNumberOfStatesException;
import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;
import de.feil.view.dialog.AlertHelper;
import javafx.stage.FileChooser;

import java.io.*;

public class SerializationController {

    private static final FileChooser fileChooser;

    static {
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("populations"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.ser", "*.ser"));
    }

    public SerializationController(ReferenceHandler referenceHandler) {
        referenceHandler.getMainController().getSerializeMenuItem().setOnAction(e -> savePopulation(referenceHandler));
        referenceHandler.getMainController().getDeserializeMenuItem().setOnAction(e -> loadPopulation(referenceHandler));
    }

    public void savePopulation(ReferenceHandler referenceHandler) {
        Automaton automaton = referenceHandler.getAutomaton();
        fileChooser.setTitle("Speichere Serialisierung");
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
                outputStream.writeObject(automaton.getCells());
            }
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Fehler beim Speichern:\n" + e);
        }
    }

    public void loadPopulation(ReferenceHandler referenceHandler) {
        Automaton automaton = referenceHandler.getAutomaton();
        fileChooser.setTitle("Lade Serialisierung");
        File file = fileChooser.showOpenDialog(referenceHandler.getMainStage());

        if (file == null) {
            return;
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            if (inputStream.readInt() != automaton.getNumberOfStates()) {
                throw new InvalidNumberOfStatesException("Die Anzahl der Zustände stimmen nicht überein!");
            }

            automaton.swapCells((Cell[][]) inputStream.readObject());
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Fehler beim Laden:\n" + e);
        }
    }
}