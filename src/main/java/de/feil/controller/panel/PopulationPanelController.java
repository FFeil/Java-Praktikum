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
            int rowStart;
            int rowEnd;
            int colStart;
            int colEnd;

            if (rowDragStart < rowCol.value1()) {
                rowStart = rowDragStart;
                rowEnd = rowCol.value1();
            } else {
                rowStart = rowCol.value1();
                rowEnd = rowDragStart;
            }
            if (columnDragStart < rowCol.value2()) {
                colStart = columnDragStart;
                colEnd = rowCol.value2();
            } else {
                colStart = rowCol.value2();
                colEnd = columnDragStart;
            }

            automaton.setState(rowStart, colStart, rowEnd + 1, colEnd + 1, getSelectedRadioButton());
        });
    }

  public void onZoomInAction(ActionEvent event) {
      if (populationPanel.zoomIn()) {
          if (controller.getZoomInButton().isDisable()) {
              controller.getZoomOutButton().setDisable(false);
              controller.getZoomOutMenuItem().setDisable(false);
          }
          automaton.notifyObserver();
      } else {
          controller.getZoomInButton().setDisable(true);
          controller.getZoomInButton().setDisable(true);
      }
  }

  public void onZoomOutAction(ActionEvent event) {
      if (populationPanel.zoomOut()) {
          if (controller.getZoomInButton().isDisable()) {
              controller.getZoomInButton().setDisable(false);
              controller.getZoomInButton().setDisable(false);
          }
          automaton.notifyObserver();
      } else {
          controller.getZoomOutButton().setDisable(true);
          controller.getZoomOutMenuItem().setDisable(true);
      }
  }

    private int getSelectedRadioButton() {
        return Integer.parseInt(((RadioButton) toggleGroup.getSelectedToggle()).getText());
    }
}
