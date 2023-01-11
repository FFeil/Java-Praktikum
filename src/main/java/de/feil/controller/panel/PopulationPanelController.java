package de.feil.controller.panel;

import de.feil.controller.references.ReferenceHandler;
import de.feil.view.dialog.AlertHelper;
import de.feil.view.panel.PopulationPanel;
import de.feil.view.panel.PopulationPanelContextMenu;
import javafx.event.ActionEvent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class PopulationPanelController {

    private final ReferenceHandler referenceHandler;

    private final PopulationPanel populationPanel;

    private int lastRowClick;
    private int lastColumnClick;


    public PopulationPanelController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;
        this.populationPanel = referenceHandler.getPopulationPanel();

        populationPanel.getCanvas().addEventHandler(MOUSE_PRESSED, this::onMousePressed);
        populationPanel.getCanvas().addEventHandler(MOUSE_DRAGGED, this::onMouseDragged);
        populationPanel.setOnContextMenuRequested(this::onContextMenuRequested);

        referenceHandler.getMainController().getZoomInButton().setOnAction(this::onZoomInAction);
        referenceHandler.getMainController().getZoomInMenuItem().setOnAction(this::onZoomInAction);
        referenceHandler.getMainController().getZoomOutButton().setOnAction(this::onZoomOutAction);
        referenceHandler.getMainController().getZoomOutMenuItem().setOnAction(this::onZoomOutAction);
    }

    private void onMousePressed(MouseEvent event) {
        populationPanel.getRowAndCol(event.getX(), event.getY()).ifPresent(rowCol -> {
            lastRowClick = rowCol.value1();
            lastColumnClick = rowCol.value2();

            if (event.getButton() == MouseButton.PRIMARY) {

                referenceHandler.getAutomaton().setState(lastRowClick, lastColumnClick,
                        referenceHandler.getStatePanelController().getSelectedState());
            }
        });
    }

    private void onMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            populationPanel.getRowAndCol(event.getX(), event.getY()).ifPresent(rowCol -> {
                int rowStart = Math.min(lastRowClick, rowCol.value1());
                int rowEnd = Math.max(lastRowClick, rowCol.value1()) + 1;
                int colStart = Math.min(lastColumnClick, rowCol.value2());
                int colEnd = Math.max(lastColumnClick, rowCol.value2()) + 1;

                // Sonst Index out of bounds Exception
                if (rowEnd > referenceHandler.getAutomaton().getNumberOfRows()) {
                    rowEnd--;
                }
                if (colEnd > referenceHandler.getAutomaton().getNumberOfColumns()) {
                    colEnd--;
                }

                referenceHandler.getAutomaton().setState(rowStart, colStart, rowEnd, colEnd,
                        referenceHandler.getStatePanelController().getSelectedState());
            });
        }
    }

    private void onContextMenuRequested(ContextMenuEvent event) {
        PopulationPanelContextMenu contextMenu = new PopulationPanelContextMenu(referenceHandler);

        for (int i = 0; i < contextMenu.getItems().size(); i ++) {
            Method method = contextMenu.getMethods().get(i);

            contextMenu.getItems().get(i).setOnAction(actionEvent -> {
                try {
                    method.invoke(referenceHandler.getAutomaton(), lastRowClick, lastColumnClick);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    AlertHelper.showError(referenceHandler.getName(),
                            "Beim Ausfuehren der Methode ist ein Fehler aufgetreten:\n" + e);
                }
            });
        }

        contextMenu.show(populationPanel.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }

    private void onZoomInAction(ActionEvent event) {
        populationPanel.zoomIn();
        populationPanel.paintCanvas();

        if (!populationPanel.canZoomIn()) {
            referenceHandler.getMainController().getZoomInButton().setDisable(true);
        }

        if (referenceHandler.getMainController().getZoomOutButton().isDisable()) {
            referenceHandler.getMainController().getZoomOutButton().setDisable(false);
            referenceHandler.getMainController().getZoomOutMenuItem().setDisable(false);
        }
    }

    private void onZoomOutAction(ActionEvent event) {
        populationPanel.zoomOut();
        populationPanel.paintCanvas();

        if (!populationPanel.canZoomOut()) {
            referenceHandler.getMainController().getZoomOutButton().setDisable(true);
            referenceHandler.getMainController().getZoomOutMenuItem().setDisable(true);
        }

        if (referenceHandler.getMainController().getZoomInButton().isDisable()) {
            referenceHandler.getMainController().getZoomInButton().setDisable(false);
            referenceHandler.getMainController().getZoomInButton().setDisable(false);
        }
    }
}
