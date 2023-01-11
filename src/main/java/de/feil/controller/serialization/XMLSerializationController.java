package de.feil.controller.serialization;

import de.feil.controller.references.ReferenceHandler;
import javafx.stage.FileChooser;

import java.io.*;

public class XMLSerializationController {

    private static FileChooser fileChooser = null;

    static {
        fileChooser = new FileChooser();
        File dir = new File(".");
        fileChooser.setInitialDirectory(dir);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.xml", "*.xml"));
    }

    public XMLSerializationController(ReferenceHandler referenceHandler) {
        referenceHandler.getMainController().getSerializeMenuItem().setOnAction(e -> savePopulation(referenceHandler));
        referenceHandler.getMainController().getDeserializeMenuItem().setOnAction(e -> loadPopulation(referenceHandler));
    }

    public void savePopulation(ReferenceHandler referenceHandler) {
        //TODO
    }

    public void loadPopulation(ReferenceHandler referenceHandler) {
        //TODO
    }
}
