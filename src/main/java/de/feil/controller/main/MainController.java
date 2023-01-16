package de.feil.controller.main;

import de.feil.controller.references.ReferenceHandler;
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

    // Serialize
    @FXML
    private MenuItem xmlSerializeMenuItem;
    @FXML
    private MenuItem serializeMenuItem;

    // Deserialize
    @FXML
    private MenuItem xmlDeserializeMenuItem;
    @FXML
    private MenuItem deserializeMenuItem;

    // Settings
    @FXML
    private MenuItem saveSettingsMenuItem;
    @FXML
    private MenuItem restoreSettingsMenuItem;
    @FXML
    private MenuItem deleteSettingsMenuItem;

    private static final FileChooser javaFileChooser;
    private final ReferenceHandler referenceHandler;
    private final Stage mainStage;

    static {
        javaFileChooser = new FileChooser();
        javaFileChooser.setTitle("Automat wählen");
        javaFileChooser.setInitialDirectory(new File("automata"));
        javaFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAVA files (*.java)", "*.java"));
    }

    public MainController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;
        this.mainStage = referenceHandler.getMainStage();

        referenceHandler.setMainController(this);
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

    public MenuItem getXmlSerializeMenuItem() {
        return xmlSerializeMenuItem;
    }

    public MenuItem getSerializeMenuItem() {
        return serializeMenuItem;
    }

    public MenuItem getXmlDeserializeMenuItem() {
        return xmlDeserializeMenuItem;
    }

    public MenuItem getDeserializeMenuItem() {
        return deserializeMenuItem;
    }

    public MenuItem getSaveSettingsMenuItem() {
        return saveSettingsMenuItem;
    }

    public MenuItem getRestoreSettingsMenuItem() {
        return restoreSettingsMenuItem;
    }

    public MenuItem getDeleteSettingsMenuItem() {
        return deleteSettingsMenuItem;
    }

    public void initialize() {
        torusToggleButton.setSelected(referenceHandler.getAutomaton().isTorus());
        torusCheckMenuItem.setSelected(referenceHandler.getAutomaton().isTorus());
    }

    private void onCloseRequest(WindowEvent event) {
        try {
            Files.delete(Paths.get("automata/" + referenceHandler.getName() + ".class"));
        } catch (IOException e) {
            AlertHelper.showError(
                    referenceHandler.getName(),"Beim Schließen des Fensters ist ein Fehler aufgetreten" + e);
        }
    }

    @FXML
    public void onNewAction() {
        Platform.runLater(() -> new NewAutomatonDialog().showAndWait().ifPresent(name -> {
            FileHelper.createFile(name);
            FileHelper.loadAutomaton(name, true).ifPresent(obj -> MVCSetCreator.create(name, obj));
        }));
    }

    @FXML
    public void onLoadAction() {
        File selectedFile = javaFileChooser.showOpenDialog(mainStage);

        if (selectedFile == null) {
            return;
        }

        String name = selectedFile.getName().replace(".java", "");

        if (!new File("automata/" + name + ".class").exists()) {
            FileHelper.loadAutomaton(name, true).ifPresent(obj -> MVCSetCreator.create(name, obj));
        } else {
            AlertHelper.showError(referenceHandler.getName(),
                    "Der ausgewählte Automat wird bereits benutzt!");
        }
    }

    @FXML
    public void onEditorAction() throws IOException {
        Path path = Paths.get("automata/" + referenceHandler.getName() + ".java");
        StringBuilder text = new StringBuilder();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int l = 0; l < lines.size(); l++) {
            text.append(lines.get(l));
            if (l < lines.size() - 1) {
                text.append(System.lineSeparator());
            }
        }

        referenceHandler.getEditorController().showStage(text.toString());
    }

    @FXML
    public void onExitAction() {
        mainStage.close();
    }

    @FXML
    public void onResetAction() {
        referenceHandler.getAutomaton().clearPopulation();
    }

    @FXML
    public void onRandomAction() {
        referenceHandler.getAutomaton().randomPopulation();
    }

    @FXML
    public void onTorusAction() {
        referenceHandler.getAutomaton().setTorus(!referenceHandler.getAutomaton().isTorus());

        torusToggleButton.setSelected(referenceHandler.getAutomaton().isTorus());
        torusCheckMenuItem.setSelected(referenceHandler.getAutomaton().isTorus());
    }

    @FXML
    public void onChangeSizeAction() {
        Platform.runLater(() ->
                new ChangeSizeDialog(referenceHandler.getAutomaton().getNumberOfRows(),
                    referenceHandler.getAutomaton().getNumberOfColumns())
                .showAndWait()
                .ifPresent(resultPair ->
                        referenceHandler.getAutomaton().changeSize(resultPair.value1(), resultPair.value2())));
    }
}
