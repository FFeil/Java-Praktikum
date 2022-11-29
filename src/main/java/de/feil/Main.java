package de.feil;

import de.feil.controller.main.Controller;
import de.feil.controller.panel.PopulationPanelController;
import de.feil.controller.panel.StatePanelController;
import de.feil.controller.simulation.SimulationController;
import de.feil.model.base.Automaton;
import de.feil.model.implementation.KruemelmonsterAutomaton;
import de.feil.view.panel.PopulationPanel;
import de.feil.view.panel.StatePanel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    // VM Args: --module-path "C:\Program Files\Java\javafx-sdk-19\lib" --add-modules javafx.controls,javafx.fxml
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        // Model
        Automaton automaton = new KruemelmonsterAutomaton(30, 30, 6, true);
        automaton.randomPopulation();

        // View
        StatePanel statePanel = new StatePanel(automaton.getNumberOfStates());
        PopulationPanel populationPanel = new PopulationPanel(automaton, statePanel.getColorPickers());

        // Controller + load fxml
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/MainView.fxml"));
        Controller controller = new Controller(automaton);
        fxmlLoader.setController(controller);
        Parent root = fxmlLoader.load();

        controller.initialize();

        // Panels zu ScrollPanes hinzufügen
        controller.getStatePanelScrollPane().setContent(statePanel);
        controller.getPopulationPanelScrollPane().setContent(populationPanel);

        // Panel Controller
        new PopulationPanelController(automaton, controller
                ,new StatePanelController(populationPanel, statePanel), populationPanel);
        // Simulation Controller
        new SimulationController(automaton, controller);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("main.css");
        stage.setScene(scene);

        stage.setTitle("Zellulärer Automat");
        stage.setMinHeight(568);
        stage.setMinWidth(736);
        stage.show();
    }
}