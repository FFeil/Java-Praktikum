package de.feil.controller.panel;

import de.feil.controller.references.ReferencesHandler;
import de.feil.view.panel.PopulationPanel;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class PopulationPanelController {

    private final ReferencesHandler referencesHandler;

    private final PopulationPanel populationPanel;

    private int rowDragStart;
    private int columnDragStart;


    public PopulationPanelController(ReferencesHandler referencesHandler) {
        this.referencesHandler = referencesHandler;
        this.populationPanel = referencesHandler.getPopulationPanel();

        populationPanel.getCanvas().addEventHandler(MOUSE_PRESSED, this::onMousePressed);
        populationPanel.getCanvas().addEventHandler(MOUSE_DRAGGED, this::onMouseDragged);

        referencesHandler.getMainController().getZoomInButton().setOnAction(this::onZoomInAction);
        referencesHandler.getMainController().getZoomInMenuItem().setOnAction(this::onZoomInAction);
        referencesHandler.getMainController().getZoomOutButton().setOnAction(this::onZoomOutAction);
        referencesHandler.getMainController().getZoomOutMenuItem().setOnAction(this::onZoomOutAction);
    }

    private void onMousePressed(MouseEvent event) {
        populationPanel.getRowAndCol(event.getX(), event.getY()).ifPresent(rowCol -> {
            rowDragStart = rowCol.value1();
            columnDragStart = rowCol.value2();

            referencesHandler.getAutomaton().setState(rowDragStart, columnDragStart,
                    referencesHandler.getStatePanelController().getSelectedState());
        });
    }

    private void onMouseDragged(MouseEvent event) {
        populationPanel.getRowAndCol(event.getX(), event.getY()).ifPresent(rowCol -> {
            int rowStart = Math.min(rowDragStart, rowCol.value1());
            int rowEnd = Math.max(rowDragStart, rowCol.value1()) + 1;
            int colStart = Math.min(columnDragStart, rowCol.value2());
            int colEnd = Math.max(columnDragStart, rowCol.value2()) + 1;

            // Sonst Index out of bounds Exception
            if (rowEnd > referencesHandler.getAutomaton().getNumberOfRows()) {
                rowEnd--;
            }
            if (colEnd > referencesHandler.getAutomaton().getNumberOfColumns()) {
                colEnd--;
            }

            referencesHandler.getAutomaton().setState(rowStart, colStart, rowEnd, colEnd,
                    referencesHandler.getStatePanelController().getSelectedState());
        });
    }

  public void onZoomInAction(ActionEvent event) {
      populationPanel.zoomIn();
      populationPanel.paintCanvas();

      if (!populationPanel.canZoomIn()) {
          referencesHandler.getMainController().getZoomInButton().setDisable(true);
      }

      if (referencesHandler.getMainController().getZoomOutButton().isDisable()) {
          referencesHandler.getMainController().getZoomOutButton().setDisable(false);
          referencesHandler.getMainController().getZoomOutMenuItem().setDisable(false);
      }
  }

  public void onZoomOutAction(ActionEvent event) {
      populationPanel.zoomOut();
      populationPanel.paintCanvas();

      if (!populationPanel.canZoomOut()) {
          referencesHandler.getMainController().getZoomOutButton().setDisable(true);
          referencesHandler.getMainController().getZoomOutMenuItem().setDisable(true);
      }

      if (referencesHandler.getMainController().getZoomInButton().isDisable()) {
          referencesHandler.getMainController().getZoomInButton().setDisable(false);
          referencesHandler.getMainController().getZoomInButton().setDisable(false);
      }
  }
}
