package de.feil.controller.main;

import de.feil.controller.editor.EditorController;
import de.feil.model.base.Automaton;
import de.feil.util.FileLoader;
import de.feil.MVCSetCreator;
import de.feil.view.dialog.ChangeSizeDialog;
import de.feil.view.dialog.ErrorAlert;
import de.feil.view.dialog.NewAutomatonDialog;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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

    private final Stage mainStage;
    private final Automaton automaton;
    private final EditorController editorController;

    public MainController(Stage mainStage, Automaton automaton, EditorController editorController) {
        this.mainStage = mainStage;
        this.automaton = automaton;
        this.editorController = editorController;
    }

    public void initialize() {
        torusToggleButton.setSelected(automaton.isTorus());
        torusCheckMenuItem.setSelected(automaton.isTorus());
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

    @FXML
    public void onNewAction() {
        Platform.runLater(() -> new NewAutomatonDialog().showAndWait().ifPresent(name -> {
            File file = new File("automata", name + ".java");
            if (!file.exists()) {
                try {
                    if (file.createNewFile()) {
                        List<String> lines = Files.readAllLines(Paths.get("automata/DefaultAutomaton"),
                                StandardCharsets.UTF_8);
                        lines.set(3, lines.get(3).replace("DefaultAutomaton", name));
                        lines.set(11, lines.get(11).replace("DefaultAutomaton", name));
                        Files.write(Path.of(file.getPath()), lines, StandardCharsets.UTF_8);

                        FileLoader.loadAutomaton(name, file).ifPresent(obj -> {
                            try {
                                new MVCSetCreator(name, obj);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });

                    }
                } catch (Exception e) {
                    ErrorAlert.show("Ups, da ist was schief gelaufen:\n" + e);
                }
            } else {
                ErrorAlert.show("Der Name ist bereits vergeben!");
            }
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
        if (selectedFile != null) {
            String name = selectedFile.getName().replace(".java", "");

            if (!new File("automata/" + name + ".class").exists()) {
                FileLoader.loadAutomaton(name, selectedFile).ifPresent(obj -> {
                    try {
                        new MVCSetCreator(name, obj);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                ErrorAlert.show("Der ausgewählte Automat wird bereits benutzt!");
            }
        }
        else {
            ErrorAlert.show("Ups, da ist was schief gelaufen!");
        }
    }

    @FXML
    public void onEditorAction() throws IOException {
        Path path = Paths.get("automata/" + editorController.getName() + ".java");
        StringBuilder text = new StringBuilder();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (int l = 0; l < lines.size(); l++) {
            text.append(lines.get(l));
            if (l < lines.size() - 1) {
                text.append(System.lineSeparator());
            }
        }

        editorController.showStage(String.valueOf(text));
    }

    @FXML
    public void onExitAction() {
        mainStage.close();
    }

    @FXML
    public void onResetAction() {
        automaton.clearPopulation();
    }

    @FXML
    public void onRandomAction() {
        automaton.randomPopulation();
    }

    @FXML
    public void onTorusAction() {
        automaton.setTorus(!automaton.isTorus());

        torusToggleButton.setSelected(automaton.isTorus());
        torusCheckMenuItem.setSelected(automaton.isTorus());
    }

    @FXML
    public void onChangeSizeAction() {
        Platform.runLater(() -> new ChangeSizeDialog(automaton.getNumberOfRows(), automaton.getNumberOfColumns())
                .showAndWait().ifPresent(resultPair -> automaton.changeSize(resultPair.value1(), resultPair.value2())));
    }
}
