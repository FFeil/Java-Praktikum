package de.feil;

import de.feil.automaton.Automaton;
import de.feil.automaton.GameOfLifeAutomaton;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class Controller {

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

    public void onZoomInAction() {
        populationPanel.zoomIn();
        populationPanel.resize(populationPanel.calcCanvasWidth(), populationPanel.calcCanvasHeight());
    }

    public void onZoomOutAction() {
        populationPanel.zoomOut();
        populationPanel.resize(populationPanel.calcCanvasWidth(), populationPanel.calcCanvasHeight());
    }
}
