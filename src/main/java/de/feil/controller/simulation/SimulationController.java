package de.feil.controller.simulation;

import de.feil.controller.references.ReferenceHandler;
import de.feil.view.dialog.AlertHelper;
import javafx.event.ActionEvent;

public class SimulationController {

    static final int DEF_SPEED = 50;
    static final int MIN_SPEED = 1;
    static final int MAX_SPEED = 100;

    private final ReferenceHandler referenceHandler;

    private SimulationThread simulationThread;
    private volatile int speed;

    public SimulationController(ReferenceHandler referenceHandler) {
        this.referenceHandler = referenceHandler;
        this.speed = 3000 / DEF_SPEED;

        referenceHandler.getMainController().getSlider().setMin(MIN_SPEED);
        referenceHandler.getMainController().getSlider().setMax(MAX_SPEED);
        referenceHandler.getMainController().getSlider().setValue(DEF_SPEED);

        referenceHandler.getMainController().getStepMenuItem().setOnAction(this::onStepAction);
        referenceHandler.getMainController().getStepButton().setOnAction(this::onStepAction);
        referenceHandler.getMainController().getStartMenuItem().setOnAction(this::onStartAction);
        referenceHandler.getMainController().getStartButton().setOnAction(this::onStartAction);
        referenceHandler.getMainController().getStopMenuItem().setOnAction(this::onStopAction);
        referenceHandler.getMainController().getStopButton().setOnAction(this::onStopAction);

        referenceHandler.getMainController().getSlider().valueProperty().addListener(
                (obs, o, n) -> speed =  3000 / n.intValue());

        simulationThread = null;
    }

    private void onStepAction(ActionEvent event) {
        try {
            referenceHandler.getAutomaton().nextGeneration();
        } catch (Throwable e) {
            e.printStackTrace();
            AlertHelper.showError("Laufzeitfehler in der transform-Methode: " + e);
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
                } catch (Throwable e) {
                    e.printStackTrace();
                    AlertHelper.showError("Laufzeitfehler in der transform-Methode:\n" + e);
                }
            }
        }
    }
}