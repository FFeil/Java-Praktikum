package de.feil.controller.simulation;

import de.feil.controller.main.MainController;
import de.feil.model.base.Automaton;
import de.feil.view.dialog.ErrorAlert;
import javafx.event.ActionEvent;

public class SimulationController {

    private Automaton automaton;
    private final MainController controller;

    final static int DEF_SPEED = 800;
    final static int MIN_SPEED = 100;
    final static int MAX_SPEED = 2000;

    private volatile int speed;

    private SimulationThread simulationThread;

    public SimulationController(Automaton automaton, MainController mainController) {
        this.automaton = automaton;
        this.controller = mainController;
        this.speed = DEF_SPEED;

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
                (obs, o, n) -> speed = Math.abs(n.intValue() - MAX_SPEED - MIN_SPEED));

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
        controller.getStepMenuItem().setDisable(true);
        controller.getStepButton().setDisable(true);
        controller.getStartMenuItem().setDisable(true);
        controller.getStartButton().setDisable(true);
        controller.getStopMenuItem().setDisable(false);
        controller.getStopButton().setDisable(false);

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

        controller.getStepMenuItem().setDisable(false);
        controller.getStepButton().setDisable(false);
        controller.getStartMenuItem().setDisable(false);
        controller.getStartButton().setDisable(false);
        controller.getStopMenuItem().setDisable(true);
        controller.getStopButton().setDisable(true);

        simulationThread = null;
    }

    class SimulationThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted()) {
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