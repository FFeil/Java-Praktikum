package de.feil.controller.references;

import de.feil.controller.database.DatabaseController;
import de.feil.controller.editor.EditorController;
import de.feil.controller.main.MainController;
import de.feil.controller.panel.StatePanelController;
import de.feil.controller.simulation.SimulationController;
import de.feil.model.base.Automaton;
import de.feil.view.panel.PopulationPanel;
import de.feil.view.panel.StatePanel;
import javafx.stage.Stage;

import java.util.List;

public class ReferenceHandler{

    private final String name;
    private final List<ReferenceHandler> referenceHandlers; // Alle ReferenceHandler

    private Automaton automaton;

    private MainController mainController;
    private StatePanelController statePanelController;
    private EditorController editorController;
    private SimulationController simulationController;
    private DatabaseController databaseController;

    private final Stage mainStage;
    private final Stage editorStage;

    private final StatePanel statePanel;
    private PopulationPanel populationPanel;

    public ReferenceHandler(String name, List<ReferenceHandler> referenceHandlers, Automaton automaton, Stage mainStage, Stage editorStage,
                            StatePanel statePanel) {
        this.name = name;
        this.referenceHandlers = referenceHandlers;
        this.automaton = automaton;
        this.mainStage = mainStage;
        this.editorStage = editorStage;
        this.statePanel = statePanel;

        referenceHandlers.add(this);
    }

    public String getName() {
        return name;
    }

    public List<ReferenceHandler> getReferenceHandlers() {
        return referenceHandlers;
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public MainController getMainController() {
        return mainController;
    }

    public StatePanelController getStatePanelController() {
        return statePanelController;
    }

    public EditorController getEditorController() {
        return editorController;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public Stage getEditorStage() {
        return editorStage;
    }

    public StatePanel getStatePanel() {
        return statePanel;
    }

    public PopulationPanel getPopulationPanel() {
        return populationPanel;
    }

    public SimulationController getSimulationController() {
        return simulationController;
    }

    public DatabaseController getDatabaseController() {
        return databaseController;
    }

    public void setAutomaton(Automaton automaton) {
        if (this.automaton == null) {
            return;
        }

        automaton.addAll(this.automaton);
        this.automaton.clear();
        this.automaton = automaton;

        mainController.initialize();
        statePanelController.updateStatePanel();
        populationPanel.paintCanvas();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setEditorController(EditorController editorController) {
        this.editorController = editorController;
    }

    public void setStatePanelController(StatePanelController statePanelController) {
        this.statePanelController = statePanelController;
    }

    public void setPopulationPanel(PopulationPanel populationPanel) {
        this.populationPanel = populationPanel;
    }

    public void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
    }

    public void setDatabaseController(DatabaseController databaseController) {
        this.databaseController = databaseController;
    }
}
