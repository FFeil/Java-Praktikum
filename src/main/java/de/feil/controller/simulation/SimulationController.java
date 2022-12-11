package de.feil.controller.simulation;

import de.feil.controller.main.MainController;
import de.feil.model.base.Automaton;
import de.feil.view.dialog.ErrorAlert;
import javafx.event.ActionEvent;

public class SimulationController {

    static final int DEF_SPEED = 4;
    static final int MIN_SPEED = 1;
    static final int MAX_SPEED = 10;

    private final Automaton automaton;
    private final MainController mainController;

    private SimulationThread simulationThread;
    private volatile int speed;

    public SimulationController(Automaton automaton, MainController mainController) {
        this.automaton = automaton;
        this.mainController = mainController;
        this.speed = 3000 / DEF_SPEED;

        mainController.getSlider().setMin(MIN_SPEED);
        mainController.getSlider().setMax(MAX_SPEED);
        mainController.getSlider().setValue(DEF_SPEED);

        mainController.getStepMenuItem().setOnAction(this::onStepAction);
        mainController.getStepButton().setOnAction(this::onStepAction);
        mainController.getStartMenuItem().setOnAction(this::onStartAction);
        mainController.getStartButton().setOnAction(this::onStartAction);
        mainController.getStopMenuItem().setOnAction(this::onStopAction);
        mainController.getStopButton().setOnAction(this::onStopAction);

        mainController.getSlider().valueProperty().addListener(
                (obs, o, n) -> speed =  3000 / n.intValue());

        simulationThread = null;
    }

    private void onStepAction(ActionEvent event) {
        try {
            automaton.nextGeneration();
        } catch (Throwable e) {
            e.printStackTrace();
            ErrorAlert.show("Laufzeitfehler in der transform-Methode: " + e);
        }
    }


    private void onStartAction(ActionEvent event) {
        mainController.getStepMenuItem().setDisable(true);
        mainController.getStepButton().setDisable(true);
        mainController.getStartMenuItem().setDisable(true);
        mainController.getStartButton().setDisable(true);
        mainController.getStopMenuItem().setDisable(false);
        mainController.getStopButton().setDisable(false);

        if (simulationThread == null) {
            simulationThread = new SimulationThread();
            simulationThread.setDaemon(true);
            simulationThread.start();
        }
    }

    private void onStopAction(ActionEvent event) {
        if (simulationThread != null) {
            simulationThread.interrupt();
            try {
                simulationThread.join();
            } catch (InterruptedException e) {
                SimulationThread.currentThread().interrupt();
            }
        }

        mainController.getStepMenuItem().setDisable(false);
        mainController.getStepButton().setDisable(false);
        mainController.getStartMenuItem().setDisable(false);
        mainController.getStartButton().setDisable(false);
        mainController.getStopMenuItem().setDisable(true);
        mainController.getStopButton().setDisable(true);

        simulationThread = null;
    }

    class SimulationThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted() && mainController.getMainStage().isShowing()) {
                try {
                    automaton.nextGeneration();
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    interrupt();
                } catch (Throwable e) {
                    e.printStackTrace();
                    ErrorAlert.show("Laufzeitfehler in der transform-Methode: " + e);
                }
            }
        }
    }
}