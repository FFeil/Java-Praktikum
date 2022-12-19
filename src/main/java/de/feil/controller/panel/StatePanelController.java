package de.feil.controller.panel;

import de.feil.controller.references.ReferenceHandler;
import de.feil.view.panel.StatePanel;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioButton;

public class StatePanelController {

    private final ReferenceHandler referenceHandler;
    private final StatePanel statePanel;

    public StatePanelController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;
        this.statePanel = referenceHandler.getStatePanel();

        referenceHandler.setStatePanelController(this);

        statePanel.getColorPickers().forEach(r -> r.addEventHandler(ActionEvent.ACTION, this::onColorPickerAction));
    }

    public int getSelectedState() {
        return Integer.parseInt(((RadioButton) statePanel.getToggleGroup().getSelectedToggle()).getText());
    }

    private void onColorPickerAction(ActionEvent event) {
        referenceHandler.getPopulationPanel().paintCanvas();
    }

    public void updateStatePanel() {
        statePanel.updatePanel(referenceHandler.getAutomaton().getNumberOfStates());
        statePanel.getColorPickers().forEach(r -> r.addEventHandler(ActionEvent.ACTION, this::onColorPickerAction));
    }
}
