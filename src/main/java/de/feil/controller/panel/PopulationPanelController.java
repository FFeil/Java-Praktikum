package de.feil.controller.panel;

import de.feil.controller.main.Controller;
import de.feil.model.base.Automaton;
import de.feil.view.panel.PopulationPanel;
import javafx.event.ActionEvent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;

import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class PopulationPanelController {

    private Automaton automaton;
    private final PopulationPanel populationPanel;
    private final ToggleGroup toggleGroup;
    private final Controller controller;

    private int rowDragStart;
    private int columnDragStart;


    public PopulationPanelController(Automaton automaton, PopulationPanel populationPanel,
                                     ToggleGroup toggleGroup, Controller controller) {
        this.automaton = automaton;
        this.populationPanel = populationPanel;
        this.toggleGroup = toggleGroup;
        this.controller = controller;

        populationPanel.getCanvas().addEventHandler(MOUSE_PRESSED, this::onMousePressed);
        populationPanel.getCanvas().addEventHandler(MOUSE_DRAGGED, this::onMouseDragged);

        controller.getZoomInButton().setOnAction(this::onZoomInAction);
        controller.getZoomInMenuItem().setOnAction(this::onZoomInAction);
        controller.getZoomOutButton().setOnAction(this::onZoomOutAction);
        controller.getZoomOutMenuItem().setOnAction(this::onZoomOutAction);
    }

    private void onMousePressed(MouseEvent event) {
        populationPanel.getRowAndCol(event.getX(), event.getY()).ifPresent(rowCol -> {
            rowDragStart = rowCol.value1();
            columnDragStart = rowCol.value2();

            automaton.setState(rowDragStart, columnDragStart, getSelectedRadioButton());
        });
    }

    private void onMouseDragged(MouseEvent event) {
        populationPanel.getRowAndCol(event.getX(), event.getY()).ifPresent(rowCol -> {
            int rowStart = Math.min(rowDragStart, rowCol.value1());
            int rowEnd = Math.max(rowDragStart, rowCol.value1()) + 1;
            int colStart = Math.min(columnDragStart, rowCol.value2());
            int colEnd = Math.max(columnDragStart, rowCol.value2()) + 1;

            // Sonst Index out of bounds Exception
            if (rowEnd > automaton.getNumberOfRows()) {
                rowEnd--;
            }
            if (colEnd > automaton.getNumberOfColumns()) {
                colEnd--;
            }

            automaton.setState(rowStart, colStart, rowEnd, colEnd, getSelectedRadioButton());
        });
    }

  public void onZoomInAction(ActionEvent event) {
      populationPanel.zoomIn();
      automaton.notifyObserver();

      if (!populationPanel.canZoomIn()) {
          controller.getZoomInButton().setDisable(true);
          controller.getZoomInButton().setDisable(true);
      }

      if (controller.getZoomOutButton().isDisable()) {
          controller.getZoomOutButton().setDisable(false);
          controller.getZoomOutMenuItem().setDisable(false);
      }
  }

  public void onZoomOutAction(ActionEvent event) {
      populationPanel.zoomOut();
      automaton.notifyObserver();

      if (!populationPanel.canZoomOut()) {
          controller.getZoomOutButton().setDisable(true);
          controller.getZoomOutMenuItem().setDisable(true);
      }

      if (controller.getZoomInButton().isDisable()) {
          controller.getZoomInButton().setDisable(false);
          controller.getZoomInButton().setDisable(false);
      }
  }

    private int getSelectedRadioButton() {
        return Integer.parseInt(((RadioButton) toggleGroup.getSelectedToggle()).getText());
    }
}
