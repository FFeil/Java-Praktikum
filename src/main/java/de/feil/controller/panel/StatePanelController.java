package de.feil.controller.panel;

import de.feil.model.base.Automaton;
import de.feil.view.panel.StatePanel;
import javafx.event.ActionEvent;

public class StatePanelController {

    private Automaton automaton;

    public StatePanelController(Automaton automaton, StatePanel statePanel) {
        this.automaton = automaton;

        statePanel.getColorPickers().forEach(r -> r.addEventHandler(ActionEvent.ACTION, this::onColorPickerAction));
    }

    private void onColorPickerAction(ActionEvent event) {
        automaton.notifyObserver();
    }
}
