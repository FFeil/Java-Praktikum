package de.feil;

import de.feil.automaton.Automaton;
import de.feil.automaton.GameOfLifeAutomaton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class Controller {

    // RadioButtons
    @FXML
    public RadioButton radioButton0;
    @FXML
    public RadioButton radioButton1;
    @FXML
    public RadioButton radioButton2;
    @FXML
    public RadioButton radioButton3;
    @FXML
    public RadioButton radioButton4;
    @FXML
    public RadioButton radioButton5;
    @FXML
    public RadioButton radioButton6;
    @FXML
    public RadioButton radioButton7;
    @FXML
    public RadioButton radioButton8;
    @FXML
    public RadioButton radioButton9;
    private ArrayList<RadioButton> radioButtons;

    // ColorPickers
    @FXML
    public ColorPicker colorPicker0;
    @FXML
    public ColorPicker colorPicker1;
    @FXML
    public ColorPicker colorPicker2;
    @FXML
    public ColorPicker colorPicker3;
    @FXML
    public ColorPicker colorPicker4;
    @FXML
    public ColorPicker colorPicker5;
    @FXML
    public ColorPicker colorPicker6;
    @FXML
    public ColorPicker colorPicker7;
    @FXML
    public ColorPicker colorPicker8;
    @FXML
    public ColorPicker colorPicker9;
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

    @FXML
    private ScrollPane scrollPane;
    private PopulationPanel populationPanel;



    private Automaton automaton;

    public void initialize() {
        automaton = new GameOfLifeAutomaton(60, 60, false);
        automaton.randomPopulation();

        radioButtons = new ArrayList<>(Arrays.asList(radioButton0, radioButton1, radioButton2, radioButton3, radioButton4,
                radioButton5, radioButton6, radioButton7, radioButton8, radioButton9));
        colorPickers = new ArrayList<>(Arrays.asList(colorPicker0, colorPicker1, colorPicker2, colorPicker3, colorPicker4,
                colorPicker5, colorPicker6, colorPicker7, colorPicker8, colorPicker9));
        ToggleGroup toggleGroup = new ToggleGroup();

        radioButtons.forEach(r -> r.setToggleGroup(toggleGroup));

        if (automaton instanceof GameOfLifeAutomaton) {
            for (int i = 2; i < 10; i++) {
                radioButtons.get(i).setDisable(true);
                colorPickers.get(i).setDisable(true);
            }
        }

        populationPanel = new PopulationPanel(automaton, colorPickers);
        scrollPane.setContent(populationPanel);
    }

    @FXML
    public void onZoomInAction() {
        if (populationPanel.zoomIn()) {
            zoomInButton.setDisable(false);
            zoomInMenuItem.setDisable(false);
        } else {
            zoomInButton.setDisable(true);
            zoomInMenuItem.setDisable(true);
        }
    }

    @FXML
    public void onZoomOutAction() {
        if (populationPanel.zoomOut()) {
            zoomOutButton.setDisable(false);
            zoomOutMenuItem.setDisable(false);
        }

        if (!populationPanel.zoomOut()) {
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
        Platform.runLater(() -> {
            Dialog<Pair<String, String>> dialog = new Dialog<>();

            dialog.setTitle("Größe ändern");
            dialog.setHeaderText("Welche Dimensionen soll der Automat haben?");

            TextField rows = new TextField();
            TextField columns = new TextField();

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

            dialog.setOnCloseRequest(event -> {
                if (!Pattern.matches("[1-4]?\\d?\\d", rows.getText())
                        || !Pattern.matches("[1-4]?\\d?\\d", columns.getText())) {
                    event.consume();
                }
            });

            GridPane grid = new GridPane();
            dialog.getDialogPane().setContent(grid);

            grid.setHgap(10);
            grid.setVgap(10);

            grid.add(new Label("Zeilen:"), 0, 0);
            grid.add(new Label("Spalten:"), 0, 1);
            grid.add(columns, 1, 1);
            grid.add(rows, 1, 0);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new Pair<>(rows.getText(), columns.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();
            result.ifPresent(dimension -> {
                automaton.changeSize(Integer.parseInt(dimension.getKey()), Integer.parseInt(dimension.getValue()));
                populationPanel.resizeCanvas();
            });
        });

    }
}
