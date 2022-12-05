package de.feil.util;

import de.feil.Main;
import de.feil.controller.editor.EditorController;
import de.feil.controller.main.MainController;
import de.feil.controller.panel.PopulationPanelController;
import de.feil.controller.panel.StatePanelController;
import de.feil.controller.simulation.SimulationController;
import de.feil.model.base.Automaton;
import de.feil.view.panel.PopulationPanel;
import de.feil.view.panel.StatePanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MVCSetCreator {


    public MVCSetCreator(String name, Automaton automaton) throws IOException {
        Stage mainStage = new Stage();

        // EditorController + load editor fxml
        Stage editorStage = new Stage();
        editorStage.initOwner(mainStage);
        FXMLLoader editorLoader = new FXMLLoader(Main.class.getResource("/fxml/EditorView.fxml"));
        EditorController editorController = new EditorController(name, editorStage);
        editorLoader.setController(editorController);
        editorStage.setScene(new Scene(editorLoader.load(), 400, 400));
        editorStage.setTitle("Editor");

        // View
        StatePanel statePanel = new StatePanel(automaton.getNumberOfStates());
        PopulationPanel populationPanel = new PopulationPanel(automaton, statePanel.getColorPickers());

        // MainController + load main fxml
        FXMLLoader mainLoader = new FXMLLoader(Main.class.getResource("/fxml/MainView.fxml"));
        MainController controller = new MainController(mainStage, automaton, editorController);
        mainLoader.setController(controller);
        Parent root = mainLoader.load();

        controller.initialize();

        // Panels zu ScrollPanes hinzufügen
        controller.getStatePanelScrollPane().setContent(statePanel);
        controller.getPopulationPanelScrollPane().setContent(populationPanel);

        // Panel Controller
        new PopulationPanelController(automaton, controller
                ,new StatePanelController(populationPanel, statePanel), populationPanel);

        // Simulation Controller
        new SimulationController(automaton, controller);

        // Init main Stage
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("/css/main.css");
        mainStage.setScene(scene);

        mainStage.setTitle("Zellulärer Automat: " + name);
        mainStage.setMinHeight(568);
        mainStage.setMinWidth(736);
        mainStage.show();
    }
}
