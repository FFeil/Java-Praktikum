package de.feil;

import de.feil.automaton.Automaton;
import de.feil.automaton.GameOfLifeAutomaton;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;

public class Controller {

    @FXML
    private ToggleButton torusToggleBotton;
    @FXML
    private CheckMenuItem torusCheckMenuItem;
    @FXML
    private ScrollPane scrollPane;
    private PopulationPanel populationPanel;

    private Automaton automaton;

    public void initialize() {
        automaton = new GameOfLifeAutomaton(60, 60, false);
        automaton.setState(0, 0, 1);
        automaton.setState(0, 25, 1);
        automaton.setState(10, 2, 1);

        populationPanel = new PopulationPanel(automaton);
        scrollPane.setContent(populationPanel);
    }

    @FXML
    public void onZoomInAction() {
        populationPanel.zoomIn();
    }

    @FXML
    public void onZoomOutAction() {
        populationPanel.zoomOut();
    }

    @FXML
    public void onResetAction() {
        automaton.clearPopulation();
        populationPanel.paintCanvas();
    }

    @FXML
    public void onRandomAction() {
        automaton.randomPopulation();
        populationPanel.paintCanvas();
    }

    @FXML
    public void onTorusAction() {

        automaton.setTorus(!automaton.isTorus());
    }
}
