package de.feil.controller.references;

import de.feil.controller.editor.EditorController;
import de.feil.controller.main.MainController;
import de.feil.controller.panel.StatePanelController;
import de.feil.model.base.Automaton;
import de.feil.view.panel.PopulationPanel;
import de.feil.view.panel.StatePanel;
import javafx.stage.Stage;

public class ReferenceHandler {

    private final String name;
    private Automaton automaton;

    private MainController mainController;
    private StatePanelController statePanelController;
    private EditorController editorController;

    private final Stage mainStage;
    private final Stage editorStage;

    private final StatePanel statePanel;
    private PopulationPanel populationPanel;

    public ReferenceHandler(String name, Automaton automaton, Stage mainStage, Stage editorStage,
                            StatePanel statePanel) {
        this.name = name;
        this.automaton = automaton;
        this.mainStage = mainStage;
        this.editorStage = editorStage;
        this.statePanel = statePanel;
    }

    public String getName() {
        return name;
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
}
