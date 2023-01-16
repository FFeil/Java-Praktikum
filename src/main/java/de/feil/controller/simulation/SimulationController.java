package de.feil.controller.simulation;

import de.feil.controller.references.ReferenceHandler;
import de.feil.view.dialog.AlertHelper;
import javafx.event.ActionEvent;

public class SimulationController {
    private final ReferenceHandler referenceHandler;

    static final int MAX_TIME = 3000;
    static final int INITIAL_TIME = 1500;
    static final int INCREMENT_TIME = 270;
    private SimulationThread simulationThread;
    private volatile int speed;

    public SimulationController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;
        this.speed = INITIAL_TIME;

        referenceHandler.setSimulationController(this);

        referenceHandler.getMainController().getSlider().setMin(1);
        referenceHandler.getMainController().getSlider().setMax(100);
        referenceHandler.getMainController().getSlider().setValue(50);

        referenceHandler.getMainController().getStepMenuItem().setOnAction(this::onStepAction);
        referenceHandler.getMainController().getStepButton().setOnAction(this::onStepAction);
        referenceHandler.getMainController().getStartMenuItem().setOnAction(this::onStartAction);
        referenceHandler.getMainController().getStartButton().setOnAction(this::onStartAction);
        referenceHandler.getMainController().getStopMenuItem().setOnAction(this::onStopAction);
        referenceHandler.getMainController().getStopButton().setOnAction(this::onStopAction);

        referenceHandler.getMainController().getSlider().valueProperty().addListener(
                (obs, o, n) -> speed =  MAX_TIME - INCREMENT_TIME * n.intValue());


        simulationThread = null;
    }

    public void setSpeed(int sliderSpeed) {
        referenceHandler.getMainController().getSlider().setValue(sliderSpeed);
    }

    private void onStepAction(ActionEvent event) {
        try {
            referenceHandler.getAutomaton().nextGeneration();
        } catch (Exception e) {
            AlertHelper.showError(referenceHandler.getName(), "Laufzeitfehler in der transform-Methode: " + e);
        }
    }

    private void onStartAction(ActionEvent event) {
        referenceHandler.getMainController().getStepMenuItem().setDisable(true);
        referenceHandler.getMainController().getStepButton().setDisable(true);
        referenceHandler.getMainController().getStartMenuItem().setDisable(true);
        referenceHandler.getMainController().getStartButton().setDisable(true);
        referenceHandler.getMainController().getStopMenuItem().setDisable(false);
        referenceHandler.getMainController().getStopButton().setDisable(false);

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

        referenceHandler.getMainController().getStepMenuItem().setDisable(false);
        referenceHandler.getMainController().getStepButton().setDisable(false);
        referenceHandler.getMainController().getStartMenuItem().setDisable(false);
        referenceHandler.getMainController().getStartButton().setDisable(false);
        referenceHandler.getMainController().getStopMenuItem().setDisable(true);
        referenceHandler.getMainController().getStopButton().setDisable(true);

        simulationThread = null;
    }

    class SimulationThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted() && referenceHandler.getMainController().getMainStage().isShowing()) {
                try {
                    referenceHandler.getAutomaton().nextGeneration();
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    interrupt();
                } catch (Exception e) {
                    AlertHelper.showError(referenceHandler.getName(), "Laufzeitfehler in der transform-Methode:\n" + e);
                }
            }
        }
    }
}