package de.feil.controller.panel;

import de.feil.controller.references.ReferencesHandler;
import de.feil.util.Observable;
import de.feil.view.panel.StatePanel;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioButton;

// Observer: PopulationPanel
public class StatePanelController extends Observable {

    private final ReferencesHandler referencesHandler;
    private final StatePanel statePanel;

    public StatePanelController(ReferencesHandler referencesHandler) {
        this.referencesHandler = referencesHandler;
        this.statePanel = referencesHandler.getStatePanel();

        referencesHandler.setStatePanelController(this);
        add(referencesHandler.getPopulationPanel());

        statePanel.getColorPickers().forEach(r -> r.addEventHandler(ActionEvent.ACTION, this::onColorPickerAction));
    }

    public int getSelectedState() {
        return Integer.parseInt(((RadioButton) statePanel.getToggleGroup().getSelectedToggle()).getText());
    }

    private void onColorPickerAction(ActionEvent event) {
        notifyObserver();
    }

    public void updateStatePanel() {
        statePanel.updatePanel(referencesHandler.getAutomaton().getNumberOfStates());
    }
}
