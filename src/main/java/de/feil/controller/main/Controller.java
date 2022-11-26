package de.feil.controller.main;

import de.feil.model.base.Automaton;
import de.feil.view.dialog.ChangeSizeDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class Controller {

    // Zoom
    @FXML
    public MenuItem zoomInMenuItem;
    @FXML
    public MenuItem zoomOutMenuItem;
    @FXML
    public Button zoomInButton;
    @FXML
    public Button zoomOutButton;

    // Torus
    @FXML
    private ToggleButton torusToggleButton;
    @FXML
    private CheckMenuItem torusCheckMenuItem;

    // ScrollPanes
    @FXML
    public ScrollPane statePanelScrollPane;
    @FXML
    private ScrollPane populationPanelScrollPane;

    private Automaton automaton;

    public Controller(Automaton automaton) {
        this.automaton = automaton;
    }

    public void initialize() {
        torusToggleButton.setSelected(automaton.isTorus());
        torusCheckMenuItem.setSelected(automaton.isTorus());
    }

    public ScrollPane getStatePanelScrollPane() {
        return statePanelScrollPane;
    }

    public ScrollPane getPopulationPanelScrollPane() {
        return populationPanelScrollPane;
    }

    public MenuItem getZoomInMenuItem() {
        return zoomInMenuItem;
    }

    public MenuItem getZoomOutMenuItem() {
        return zoomOutMenuItem;
    }

    public Button getZoomInButton() {
        return zoomInButton;
    }

    public Button getZoomOutButton() {
        return zoomOutButton;
    }

    @FXML
    public void onResetAction() {
        automaton.clearPopulation();
    }

    @FXML
    public void onRandomAction() {
        automaton.randomPopulation();
    }

    @FXML
    public void onTorusAction() {
        automaton.setTorus(!automaton.isTorus());

        torusToggleButton.setSelected(automaton.isTorus());
        torusCheckMenuItem.setSelected(automaton.isTorus());
    }

    @FXML
    public void onChangeSizeAction() {
        Platform.runLater(() -> new ChangeSizeDialog(automaton.getNumberOfRows(), automaton.getNumberOfColumns())
                .showAndWait().ifPresent(resultPair -> automaton.changeSize(resultPair.value1(), resultPair.value2())));
    }
}
