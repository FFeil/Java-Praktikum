package de.feil.controller.serialization;

import de.feil.controller.references.ReferenceHandler;
import de.feil.controller.serialization.exception.CellArrayNotInitializedException;
import de.feil.controller.serialization.exception.InvalidNumberOfStatesException;
import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;
import de.feil.view.dialog.AlertHelper;
import javafx.stage.FileChooser;

import javax.xml.stream.*;
import java.io.*;

public class XMLSerializationController {

    private static final FileChooser fileChooser;

    static {
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("populations"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.xml", "*.xml"));
    }

    public XMLSerializationController(ReferenceHandler referenceHandler) {
        referenceHandler.getMainController().getXmlSerializeMenuItem().setOnAction(e -> savePopulation(referenceHandler));
        referenceHandler.getMainController().getXmlDeserializeMenuItem().setOnAction(e -> loadPopulation(referenceHandler));
    }

    public void savePopulation(ReferenceHandler referenceHandler) {
        fileChooser.setTitle("Speichere XML-Serialisierung");
        File file = fileChooser.showSaveDialog(referenceHandler.getMainStage());

        if (file == null) {
            return;
        }

        if (!file.getName().endsWith(".xml")) {
            file = new File(file.getAbsolutePath().concat(".xml"));
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            writeXML(referenceHandler, outputStream);
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Fehler beim Laden der Population:\n" + e);
        }
    }


    public void loadPopulation(ReferenceHandler referenceHandler) {
        fileChooser.setTitle("Lade XML-Serialisierung");
        File file = fileChooser.showOpenDialog(referenceHandler.getMainStage());

        if (file == null) {
            return;
        }

        try (FileInputStream inputStream = new FileInputStream(file)) {
            loadXML(referenceHandler, inputStream);
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Fehler beim Laden der Population:\n" + e);
        }
    }

    // XML-Format
    // <?xml version="1.0" ?>
    // <automaton numberOfStates="2" numberOfRows="3" numberOfColumns="2">
    // <cell row="0" column="0" state="0"></cell>                               (*)
    // <cell row="0" column="1" state="1"></cell>
    // ...
    // </automaton>                                                             (**)
    private static void writeXML(ReferenceHandler referenceHandler, OutputStream outputStream) throws Exception {
        Automaton automaton = referenceHandler.getAutomaton();
        int numberOfRows = automaton.getNumberOfRows();
        int numberOfCols = automaton.getNumberOfColumns();
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer = factory.createXMLStreamWriter(outputStream);

        try {
            final String NEWLINE = System.lineSeparator();
            synchronized (automaton) {
                writer.writeStartDocument();
                writer.writeCharacters(NEWLINE);

                // Automaten Metadaten speichern
                writer.writeStartElement("automaton");
                writer.writeAttribute("numberOfStates", String.valueOf(automaton.getNumberOfStates()));
                writer.writeAttribute("numberOfRows", String.valueOf(numberOfRows));
                writer.writeAttribute("numberOfColumns", String.valueOf(numberOfCols));
                writer.writeCharacters(NEWLINE);

                // Cell-Array speichern
                for (int i = 0; i < numberOfRows; i++) {
                    for (int j = 0; j < numberOfCols; j++) {
                        writer.writeStartElement("cell");
                        writer.writeAttribute("row", String.valueOf(i));
                        writer.writeAttribute("column", String.valueOf(j));
                        writer.writeAttribute("state", String.valueOf(automaton.getCells()[i][j].getState()));
                        writer.writeEndElement(); // Von (*)
                        writer.writeCharacters(NEWLINE);
                    }
                }

                writer.writeEndElement(); // Von (**)
                writer.writeCharacters(NEWLINE);
                writer.writeEndDocument();
            }
        } finally {
            writer.close();
        }
    }

    private void loadXML(ReferenceHandler referenceHandler, InputStream inputStream) throws Exception {
        Automaton automaton = referenceHandler.getAutomaton();
        XMLStreamReader parser = null;

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
            parser = factory.createXMLStreamReader(inputStream);
            Cell[][] cells = null;

            while (parser.hasNext()) {
                if (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    String element = parser.getLocalName();
                    if ("automaton".equals(element)) {
                        // Exception, wenn #Zuständen nicht passend
                        int numberOfStates = Integer.parseInt(parser.getAttributeValue(null,
                                "numberOfStates"));
                        if (numberOfStates != automaton.getNumberOfStates()) {
                            throw new InvalidNumberOfStatesException("Die Anzahl der Zustände stimmen nicht überein!");
                        }

                        // Sonst Cell-Array initialisieren
                        int numberOfRows = Integer.parseInt(parser.getAttributeValue(null,
                                "numberOfRows"));
                        int numberOfCols = Integer.parseInt(parser.getAttributeValue(null,
                                "numberOfColumns"));
                        cells = new Cell[numberOfRows][numberOfCols];
                    } else if ("cell".equals(element)) {
                        if (cells == null) {
                            throw new CellArrayNotInitializedException();
                        }

                        // Cell-Array füllen
                        int row = Integer.parseInt(parser.getAttributeValue(null, "row"));
                        int column = Integer.parseInt(parser.getAttributeValue(null, "column"));
                        int state = Integer.parseInt(parser.getAttributeValue(null, "state"));

                        cells[row][column] = new Cell(state);
                    }
                }
                parser.next();
            }

            if (cells == null) {
                throw new CellArrayNotInitializedException();
            }

            automaton.swapCells(cells);
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Fehler beim Laden der Population:\n" + e);
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }
}
