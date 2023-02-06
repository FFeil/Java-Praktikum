package de.feil;

import de.feil.controller.database.DatabaseController;
import de.feil.controller.editor.EditorController;
import de.feil.controller.language.LanguageController;
import de.feil.controller.main.MainController;
import de.feil.controller.panel.PopulationPanelController;
import de.feil.controller.panel.StatePanelController;
import de.feil.controller.references.ReferenceHandler;
import de.feil.controller.serialization.SerializationController;
import de.feil.controller.serialization.XMLSerializationController;
import de.feil.controller.simulation.SimulationController;
import de.feil.model.base.Automaton;
import de.feil.view.dialog.AlertHelper;
import de.feil.view.panel.PopulationPanel;
import de.feil.view.panel.StatePanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MVCSetCreator {

    private MVCSetCreator() {}

    public static void create(String name, List<ReferenceHandler> referenceHandlers, Automaton automaton) {
        try {
            // View: Stages + StatePanel
            Stage mainStage = new Stage();
            Stage editorStage = new Stage();
            editorStage.initOwner(mainStage);
            StatePanel statePanel = new StatePanel(automaton.getNumberOfStates());

            // ReferenceHandler
            ReferenceHandler referenceHandler = new ReferenceHandler(name, referenceHandlers, automaton, mainStage, editorStage,
                    statePanel);

            // View: PopulationPanel
            PopulationPanel populationPanel = new PopulationPanel(referenceHandler, statePanel.getColorPickers());

            // Controller: Editor + Main
            EditorController editorController = new EditorController(referenceHandler);
            MainController mainController = new MainController(referenceHandler);

            // Load EditorView.fxml
            FXMLLoader editorLoader = new FXMLLoader(Main.class.getResource("/fxml/EditorView.fxml"));
            editorLoader.setController(editorController);

            // Load MainView.fxml
            FXMLLoader mainLoader = new FXMLLoader(Main.class.getResource("/fxml/MainView.fxml"));
            mainLoader.setController(mainController);
            Parent mainRoot = mainLoader.load();
            mainController.initialize();

            // Controller: StatePanel + PopulationPanel + Simulation + Serialization + XML-Serialization + Database
            new StatePanelController(referenceHandler);
            new PopulationPanelController(referenceHandler);
            new SimulationController(referenceHandler);
            new SerializationController(referenceHandler);
            new XMLSerializationController(referenceHandler);
            new DatabaseController(referenceHandler);

            // Panels zu ScrollPanes hinzufügen
            mainController.getStatePanelScrollPane().setContent(statePanel);
            mainController.getPopulationPanelScrollPane().setContent(populationPanel);

            // Initialize language menu + bind string properties
            mainController.intLanguageMenu();
            mainController.bindTextProperties();

            // Controller: Language
            new LanguageController(referenceHandler);

            // Init main Stage
            Scene scene = new Scene(mainRoot, 800, 600);
            scene.getStylesheets().add("/css/main.css");
            mainStage.setScene(scene);

            mainStage.setMinHeight(568);
            mainStage.setMinWidth(736);
            mainStage.show();

            // Init editor Stage
            editorStage.setScene(new Scene(editorLoader.load(), 400, 400));
            editorStage.setTitle("Editor");
            editorStage.setMinHeight(300);
            editorStage.setMinWidth(300);
        } catch (IOException e) {
            AlertHelper.showError(name, "Beim Erstellen eines neuen Fensters ist ein Fehler aufgetreten:\n" + e);
        }
    }
}
