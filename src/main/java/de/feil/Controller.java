package de.feil;

import de.feil.automaton.Automaton;
import de.feil.automaton.KruemelmonsterAutomaton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class Controller {

    // RadioButtons
    @FXML
    public RadioButton radioButton0;
    @FXML
    public RadioButton radioButton1;
    private ArrayList<RadioButton> radioButtons;

    // ColorPickers
    @FXML
    public ColorPicker colorPicker0;
    @FXML
    public ColorPicker colorPicker1;
    private ArrayList<ColorPicker> colorPickers;

    // Zoom
    @FXML
    public MenuItem zoomInMenuItem;
    @FXML
    public MenuItem zoomOutMenuItem;
    @FXML
    public Button zoomInButton;
    @FXML
    public Button zoomOutButton;

    // Torus
    @FXML
    private ToggleButton torusToggleButton;
    @FXML
    private CheckMenuItem torusCheckMenuItem;

    // Population Area
    @FXML
    private ScrollPane scrollPane;
    private PopulationPanel populationPanel;
    private Automaton automaton;

    @FXML
    public VBox statePanel;

    public void initialize() {
        automaton = new KruemelmonsterAutomaton(60, 60, 50, false);
        automaton.randomPopulation();

        radioButtons = new ArrayList<>(Arrays.asList(radioButton0, radioButton1));
        colorPickers = new ArrayList<>(Arrays.asList(colorPicker0, colorPicker1));

        createStatePanel();

        populationPanel = new PopulationPanel(automaton, colorPickers);
        scrollPane.setContent(populationPanel);
    }

    private void createStatePanel() {
        for (int i = 2; i < automaton.getNumberOfStates(); i++) {
            HBox hBox = new HBox();
            hBox.setSpacing(10);

            RadioButton radioButton = new RadioButton("" + i);
            radioButton.setMinWidth(47);
            radioButton.setMaxWidth(47);
            HBox.setMargin(radioButton, new Insets(3, 0, 0, 0));
            ColorPicker colorPicker = new ColorPicker(new Color(Math.random(), Math.random(), Math.random(),Math.random()));

            hBox.getChildren().addAll(radioButton, colorPicker);
            statePanel.getChildren().add(hBox);
            radioButtons.add(radioButton);
            colorPickers.add(colorPicker);
        }

        ToggleGroup toggleGroup = new ToggleGroup();
        radioButtons.forEach(r -> {
            r.setToggleGroup(toggleGroup);
            r.setOnAction(this::onRadioButtonAction);
        });
    }

    @FXML
    public void onZoomInAction() {
        if (populationPanel.zoomIn()) {
            if (zoomOutButton.isDisable()) {
                zoomOutButton.setDisable(false);
                zoomOutMenuItem.setDisable(false);
            }
        } else {
            zoomInButton.setDisable(true);
            zoomInMenuItem.setDisable(true);
        }
    }

    @FXML
    public void onZoomOutAction() {
        if (populationPanel.zoomOut()) {
            if (zoomInButton.isDisable()) {
                zoomInButton.setDisable(false);
                zoomInMenuItem.setDisable(false);
            }
        } else {
            zoomOutButton.setDisable(true);
            zoomOutMenuItem.setDisable(true);
        }
    }

    @FXML
    public void onResetAction() {
        automaton.clearPopulation();
        populationPanel.paintCanvas();
    }

    @FXML
    public void onRandomAction() {
        automaton.randomPopulation();
        populationPanel.paintCanvas();
    }

    @FXML
    public void onTorusAction() {
        automaton.setTorus(!automaton.isTorus());

        torusToggleButton.setSelected(automaton.isTorus());
        torusCheckMenuItem.setSelected(automaton.isTorus());
    }

    @FXML
    public void onRadioButtonAction(ActionEvent event) {
        populationPanel.setSelectedRadioButton(Integer.parseInt(((RadioButton) event.getSource()).getText()));
    }

    @FXML
    public void onColorPickerAction() {
        populationPanel.paintCanvas();
    }

    @FXML
    public void onChangeSizeAction() {
        Platform.runLater(() -> new ChangeSizeDialog().showAndWait().ifPresent(resultPair -> {
            automaton.changeSize(resultPair.getKey(), resultPair.getValue());
            populationPanel.resizeCanvas();
        }));
    }
}
