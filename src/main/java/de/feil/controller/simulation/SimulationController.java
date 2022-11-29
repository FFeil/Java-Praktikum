package de.feil.controller.simulation;

import de.feil.controller.main.Controller;
import de.feil.model.base.Automaton;
import javafx.event.ActionEvent;

public class SimulationController {

    private Automaton automaton;
    private final Controller controller;

    final static int DEF_SPEED = 1300;
    final static int MIN_SPEED = 100;
    final static int MAX_SPEED = 2000;

    private volatile int speed;

    private SimulationThread simulationThread;

    public SimulationController(Automaton automaton, Controller controller) {
        this.automaton = automaton;
        this.controller = controller;
        this.speed = DEF_SPEED;

        controller.getSlider().setMin(MIN_SPEED);
        controller.getSlider().setMax(MAX_SPEED);
        controller.getSlider().setValue(DEF_SPEED);

        controller.getStepMenuItem().setOnAction(this::onStepAction);
        controller.getStepButton().setOnAction(this::onStepAction);
        controller.getStartMenuItem().setOnAction(this::onStartAction);
        controller.getStartButton().setOnAction(this::onStartAction);
        controller.getStopMenuItem().setOnAction(this::onStopAction);
        controller.getStopButton().setOnAction(this::onStopAction);

        controller.getSlider().valueProperty().addListener((obs, o, n) -> speed = n.intValue());

        simulationThread = null;
    }

    private void onStepAction(ActionEvent event) {
        try {
            automaton.nextGeneration();
        } catch (Throwable e) {
            e.printStackTrace();
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

                    Thread.sleep(Math.abs(speed - MAX_SPEED - MIN_SPEED));
                } catch (InterruptedException e) {
                    interrupt();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}