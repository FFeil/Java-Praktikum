package de.feil.controller.main;

import de.feil.controller.references.ReferenceHandler;
import de.feil.controller.resource.ResourcesController;
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

    @FXML
    private Label helloLabel;

    // Menu: Automaton
    @FXML
    private Menu automatonMenu;
        @FXML
        private MenuItem newAutomatonMenuItem;
        @FXML
        private MenuItem loadAutomatonMenuItem;
        @FXML
        private MenuItem stopAutomatonMenuItem;

    // Menu: Population
        @FXML
        private MenuItem changeSizeMenuItem;
        @FXML
        private MenuItem resetPopulationMenuItem;
        @FXML
        private MenuItem randomPopulationMenuItem;
        @FXML
        private CheckMenuItem torusCheckMenuItem;
        @FXML
        private MenuItem zoomInMenuItem;
        @FXML
        private MenuItem zoomOutMenuItem;
        // Submenu: Save
        @FXML
        private Menu savePopulationMenu;
            @FXML
            private MenuItem xmlSerializeMenuItem;
            @FXML
            private MenuItem serializeMenuItem;
        // Submenu: Load
        @FXML
        private Menu loadPopulationMenu;
            @FXML
            private MenuItem xmlDeserializeMenuItem;
            @FXML
            private MenuItem deserializeMenuItem;
        @FXML
        private MenuItem printMenuItem;

    // Menu: Simulation
        @FXML
        private MenuItem stepMenuItem;
        @FXML
        private MenuItem startMenuItem;
        @FXML
        private MenuItem stopMenuItem;

    // Menu: Settings
    @FXML
    private Menu settingsMenu;
        @FXML
        private MenuItem saveSettingsMenuItem;
        @FXML
        private MenuItem restoreSettingsMenuItem;
        @FXML
        private MenuItem deleteSettingsMenuItem;

    // Menu: Language
    @FXML
    private Menu languageMenu;
        @FXML
        private RadioMenuItem languageEnglishRadioMenuItem;
        @FXML
        private RadioMenuItem languageGermanRadioMenuItem;
    private ToggleGroup languageGroup;

    // ToolBar
    @FXML
    private ToggleButton torusToggleButton;
    @FXML
    private Button zoomInButton;
    @FXML
    private Button zoomOutButton;
    @FXML
    private Button stepButton;
    @FXML
    private Button startButton;
    @FXML
    private Button stopButton;
    @FXML
    private Slider slider;

    // ScrollPanes
    @FXML
    private ScrollPane statePanelScrollPane;
    @FXML
    private ScrollPane populationPanelScrollPane;

    private static final FileChooser javaFileChooser;
    private final ReferenceHandler referenceHandler;
    private final Stage mainStage;
    private final ResourcesController resourcesController;

    static {
        javaFileChooser = new FileChooser();
        javaFileChooser.setTitle("Automat wählen");
        javaFileChooser.setInitialDirectory(new File("automata"));
        javaFileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JAVA files (*.java)", "*.java"));
    }

    public MainController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;
        mainStage = referenceHandler.getMainStage();
        resourcesController = ResourcesController.getResourceController();

        referenceHandler.setMainController(this);

        mainStage.setOnCloseRequest(this::onCloseRequest);
    }

    public MenuItem getZoomInMenuItem() {
        return zoomInMenuItem;
    }

    public MenuItem getZoomOutMenuItem() {
        return zoomOutMenuItem;
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

    public MenuItem getStepMenuItem() {
        return stepMenuItem;
    }

    public MenuItem getStartMenuItem() {
        return startMenuItem;
    }

    public MenuItem getStopMenuItem() {
        return stopMenuItem;
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

    public RadioMenuItem getLanguageEnglishRadioMenuItem() {
        return languageEnglishRadioMenuItem;
    }

    public RadioMenuItem getLanguageGermanRadioMenuItem() {
        return languageGermanRadioMenuItem;
    }

    public Button getZoomInButton() {
        return zoomInButton;
    }

    public Button getZoomOutButton() {
        return zoomOutButton;
    }

    public Button getStepButton() {
        return stepButton;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getStopButton() {
        return stopButton;
    }

    public Slider getSlider() {
        return slider;
    }

    public ScrollPane getStatePanelScrollPane() {
        return statePanelScrollPane;
    }

    public ScrollPane getPopulationPanelScrollPane() {
        return populationPanelScrollPane;
    }

    public ReferenceHandler getReferenceHandler() {
        return referenceHandler;
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public ToggleGroup getLanguageGroup() {
        return languageGroup;
    }

    public void initialize() {
        torusToggleButton.setSelected(referenceHandler.getAutomaton().isTorus());
        torusCheckMenuItem.setSelected(referenceHandler.getAutomaton().isTorus());
    }

    private void onCloseRequest(WindowEvent event) {
        try {
            referenceHandler.getReferenceHandlers().remove(referenceHandler);

            if (referenceHandler.getReferenceHandlers().isEmpty()) {
                referenceHandler.getDatabaseController().shutdown();
            }

            Files.delete(Paths.get("automata/" + referenceHandler.getName() + ".class"));
        } catch (IOException e) {
            AlertHelper.showError(
                    referenceHandler.getName(),"Beim Schließen des Fensters ist ein Fehler aufgetreten:\n" + e);
        }
    }

    @FXML
    public void onNewAction() {
        Platform.runLater(() -> new NewAutomatonDialog().showAndWait().ifPresent(name -> {
            FileHelper.createFile(name);
            FileHelper.loadAutomaton(name, true)
                    .ifPresent(obj -> MVCSetCreator.create(name, referenceHandler.getReferenceHandlers(), obj));
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
            FileHelper.loadAutomaton(name, true)
                    .ifPresent(obj -> MVCSetCreator.create(name, referenceHandler.getReferenceHandlers(), obj));
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

    public void intLanguageMenu() {
        languageGroup = new ToggleGroup();

        languageEnglishRadioMenuItem.setToggleGroup(this.languageGroup);
        languageEnglishRadioMenuItem
           .setSelected(ResourcesController.getResourceController().getLocale().getLanguage().equals("en"));

        languageGermanRadioMenuItem.setToggleGroup(this.languageGroup);
        languageGermanRadioMenuItem
           .setSelected(ResourcesController.getResourceController().getLocale().getLanguage().equals("de"));
    }


    public void bindTextProperties() { // Von helloLabel, Titel mainStage, MenuBar
        mainStage.titleProperty().bind(resourcesController.i18n("title").concat(referenceHandler.getName()));
        helloLabel.textProperty().bind(resourcesController.i18n("hello"));

        // Automaton
        automatonMenu.textProperty().bind(resourcesController.i18n("automatonMenu"));
        newAutomatonMenuItem.textProperty().bind(resourcesController.i18n("newAutomatonMenuItem"));
        loadAutomatonMenuItem.textProperty().bind(resourcesController.i18n("loadAutomatonMenuItem"));
        stopAutomatonMenuItem.textProperty().bind(resourcesController.i18n("stopAutomatonMenuItem"));

        // Population
        changeSizeMenuItem.textProperty().bind(resourcesController.i18n("changeSizeMenuItem"));
        resetPopulationMenuItem.textProperty().bind(resourcesController.i18n("resetPopulationMenuItem"));
        randomPopulationMenuItem.textProperty().bind(resourcesController.i18n("randomPopulationMenuItem"));
        zoomInMenuItem.textProperty().bind(resourcesController.i18n("zoomInMenuItem"));
        zoomOutMenuItem.textProperty().bind(resourcesController.i18n("zoomOutMenuItem"));
        savePopulationMenu.textProperty().bind(resourcesController.i18n("savePopulationMenu"));
        xmlSerializeMenuItem.textProperty().bind(resourcesController.i18n("xmlSerializeMenuItem"));
        serializeMenuItem.textProperty().bind(resourcesController.i18n("serializeMenuItem"));
        loadPopulationMenu.textProperty().bind(resourcesController.i18n("loadPopulationMenu"));
        xmlDeserializeMenuItem.textProperty().bind(resourcesController.i18n("xmlDeserializeMenuItem"));
        deserializeMenuItem.textProperty().bind(resourcesController.i18n("deserializeMenuItem"));
        printMenuItem.textProperty().bind(resourcesController.i18n("printMenuItem"));

        // Simulation
        stepMenuItem.textProperty().bind(resourcesController.i18n("stepMenuItem"));
        startMenuItem.textProperty().bind(resourcesController.i18n("startMenuItem"));
        stopMenuItem.textProperty().bind(resourcesController.i18n("stopMenuItem"));

        // Settings
        settingsMenu.textProperty().bind(resourcesController.i18n("settingsMenu"));
        saveSettingsMenuItem.textProperty().bind(resourcesController.i18n("saveSettingMenuItem"));
        restoreSettingsMenuItem.textProperty().bind(resourcesController.i18n("restoreSettingMenuItems"));
        deleteSettingsMenuItem.textProperty().bind(resourcesController.i18n("deleteSettingsMenuItem"));

        //Language
        languageMenu.textProperty().bind(resourcesController.i18n("languageMenu"));
        languageEnglishRadioMenuItem.textProperty().bind(resourcesController.i18n("languageEnglishRadioMenuItem"));
        languageGermanRadioMenuItem.textProperty().bind(resourcesController.i18n("languageGermanRadioMenuItem"));
    }
}
