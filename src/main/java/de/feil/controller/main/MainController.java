package de.feil.controller.main;

import de.feil.controller.references.ReferencesHandler;
import de.feil.util.FileHelper;
import de.feil.MVCSetCreator;
import de.feil.view.dialog.ChangeSizeDialog;
import de.feil.view.dialog.AlertHelper;
import de.feil.view.dialog.NewAutomatonDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class MainController {

    // Zoom in
    @FXML
    private MenuItem zoomInMenuItem;
    @FXML
    private Button zoomInButton;

    // Zoom out
    @FXML
    private MenuItem zoomOutMenuItem;
    @FXML
    private Button zoomOutButton;

    // Step
    @FXML
    private MenuItem stepMenuItem;
    @FXML
    private Button stepButton;

    // Start
    @FXML
    private MenuItem startMenuItem;
    @FXML
    private Button startButton;

    // Stop
    @FXML
    private MenuItem stopMenuItem;
    @FXML
    private Button stopButton;

    // Slider
    @FXML
    private Slider slider;

    // Torus
    @FXML
    private ToggleButton torusToggleButton;
    @FXML
    private CheckMenuItem torusCheckMenuItem;

    // ScrollPanes
    @FXML
    private ScrollPane statePanelScrollPane;
    @FXML
    private ScrollPane populationPanelScrollPane;

    private final ReferencesHandler referencesHandler;

    private final Stage mainStage;

    public MainController(ReferencesHandler referencesHandler) {
        this.referencesHandler = referencesHandler;
        this.mainStage = referencesHandler.getMainStage();

        referencesHandler.setMainController(this);
        mainStage.setOnCloseRequest(this::onCloseRequest);
    }

    public ScrollPane getStatePanelScrollPane() {
        return statePanelScrollPane;
    }

    public ScrollPane getPopulationPanelScrollPane() {
        return populationPanelScrollPane;
    }

    public MenuItem getZoomInMenuItem() {
        return zoomInMenuItem;
    }

    public MenuItem getZoomOutMenuItem() {
        return zoomOutMenuItem;
    }

    public Button getZoomInButton() {
        return zoomInButton;
    }

    public Button getZoomOutButton() {
        return zoomOutButton;
    }

    public MenuItem getStepMenuItem() {
        return stepMenuItem;
    }

    public Button getStepButton() {
        return stepButton;
    }

    public MenuItem getStartMenuItem() {
        return startMenuItem;
    }

    public Button getStartButton() {
        return startButton;
    }

    public MenuItem getStopMenuItem() {
        return stopMenuItem;
    }

    public Button getStopButton() {
        return stopButton;
    }

    public Slider getSlider() {
        return slider;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void initialize() {
        torusToggleButton.setSelected(referencesHandler.getAutomaton().isTorus());
        torusCheckMenuItem.setSelected(referencesHandler.getAutomaton().isTorus());
    }

    private void onCloseRequest(WindowEvent event) {
        try {
            Files.delete(Paths.get("automata/" + referencesHandler.getName() + ".class"));
        } catch (IOException e) {
            AlertHelper.showError("Ups, da ist was schief gelaufen:\n" + e);
        }
    }

    @FXML
    public void onNewAction() {
        Platform.runLater(() -> new NewAutomatonDialog().showAndWait().ifPresent(name -> {
            FileHelper.createFile(name);
            FileHelper.loadAutomaton(name).ifPresent(obj -> MVCSetCreator.create(name, obj));
        }));
    }

    @FXML
    public void onLoadAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Automat wählen");
        fileChooser.setInitialDirectory(new File("automata"));
        FileChooser.ExtensionFilter javaFilter = new FileChooser.ExtensionFilter("JAVA files (*.java)", "*.java");
        fileChooser.getExtensionFilters().add(javaFilter);

        File selectedFile = fileChooser.showOpenDialog(mainStage);

        if (selectedFile == null) {
            return;
        }

        String name = selectedFile.getName().replace(".java", "");

        if (!new File("automata/" + name + ".class").exists()) {
            FileHelper.loadAutomaton(name).ifPresent(obj -> MVCSetCreator.create(name, obj));
        } else {
            AlertHelper.showError("Der ausgewählte Automat wird bereits benutzt!");
        }
    }

    @FXML
    public void onEditorAction() throws IOException {
        Path path = Paths.get("automata/" + referencesHandler.getName() + ".java");
        StringBuilder text = new StringBuilder();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int l = 0; l < lines.size(); l++) {
            text.append(lines.get(l));
            if (l < lines.size() - 1) {
                text.append(System.lineSeparator());
            }
        }

        referencesHandler.getEditorController().showStage(text.toString());
    }

    @FXML
    public void onExitAction() {
        mainStage.close();
    }

    @FXML
    public void onResetAction() {
        referencesHandler.getAutomaton().clearPopulation();
    }

    @FXML
    public void onRandomAction() {
        referencesHandler.getAutomaton().randomPopulation();
    }

    @FXML
    public void onTorusAction() {
        referencesHandler.getAutomaton().setTorus(!referencesHandler.getAutomaton().isTorus());

        torusToggleButton.setSelected(referencesHandler.getAutomaton().isTorus());
        torusCheckMenuItem.setSelected(referencesHandler.getAutomaton().isTorus());
    }

    @FXML
    public void onChangeSizeAction() {
        Platform.runLater(() -> new ChangeSizeDialog(referencesHandler.getAutomaton().getNumberOfRows(),
                    referencesHandler.getAutomaton().getNumberOfColumns())
                .showAndWait()
                .ifPresent(resultPair ->
                        referencesHandler.getAutomaton().changeSize(resultPair.value1(), resultPair.value2())));
    }
}
