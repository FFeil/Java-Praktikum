package de.feil.controller.panel;

import de.feil.util.Observable;
import de.feil.view.panel.PopulationPanel;
import de.feil.view.panel.StatePanel;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioButton;

// Observer: PopulationPanel
public class StatePanelController extends Observable {

    private final StatePanel statePanel;

    public StatePanelController(PopulationPanel populationPanel, StatePanel statePanel) {
        this.statePanel = statePanel;
        add(populationPanel);

        statePanel.getColorPickers().forEach(r -> r.addEventHandler(ActionEvent.ACTION, this::onColorPickerAction));
    }

    public int getSelectedState() {
        return Integer.parseInt(((RadioButton) statePanel.getToggleGroup().getSelectedToggle()).getText());
    }

    private void onColorPickerAction(ActionEvent event) {
        notifyObserver();
    }
}
