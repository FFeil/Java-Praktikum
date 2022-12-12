package de.feil.controller.simulation;

import de.feil.controller.references.ReferencesHandler;
import de.feil.view.dialog.AlertHelper;
import javafx.event.ActionEvent;

public class SimulationController {

    static final int DEF_SPEED = 4;
    static final int MIN_SPEED = 1;
    static final int MAX_SPEED = 10;

    private final ReferencesHandler referencesHandler;

    private SimulationThread simulationThread;
    private volatile int speed;

    public SimulationController(ReferencesHandler referencesHandler) {
        this.referencesHandler = referencesHandler;
        this.speed = 3000 / DEF_SPEED;

        referencesHandler.getMainController().getSlider().setMin(MIN_SPEED);
        referencesHandler.getMainController().getSlider().setMax(MAX_SPEED);
        referencesHandler.getMainController().getSlider().setValue(DEF_SPEED);

        referencesHandler.getMainController().getStepMenuItem().setOnAction(this::onStepAction);
        referencesHandler.getMainController().getStepButton().setOnAction(this::onStepAction);
        referencesHandler.getMainController().getStartMenuItem().setOnAction(this::onStartAction);
        referencesHandler.getMainController().getStartButton().setOnAction(this::onStartAction);
        referencesHandler.getMainController().getStopMenuItem().setOnAction(this::onStopAction);
        referencesHandler.getMainController().getStopButton().setOnAction(this::onStopAction);

        referencesHandler.getMainController().getSlider().valueProperty().addListener(
                (obs, o, n) -> speed =  3000 / n.intValue());

        simulationThread = null;
    }

    private void onStepAction(ActionEvent event) {
        try {
            referencesHandler.getAutomaton().nextGeneration();
        } catch (Throwable e) {
            e.printStackTrace();
            AlertHelper.showError("Laufzeitfehler in der transform-Methode: " + e);
        }
    }


    private void onStartAction(ActionEvent event) {
        referencesHandler.getMainController().getStepMenuItem().setDisable(true);
        referencesHandler.getMainController().getStepButton().setDisable(true);
        referencesHandler.getMainController().getStartMenuItem().setDisable(true);
        referencesHandler.getMainController().getStartButton().setDisable(true);
        referencesHandler.getMainController().getStopMenuItem().setDisable(false);
        referencesHandler.getMainController().getStopButton().setDisable(false);

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

        referencesHandler.getMainController().getStepMenuItem().setDisable(false);
        referencesHandler.getMainController().getStepButton().setDisable(false);
        referencesHandler.getMainController().getStartMenuItem().setDisable(false);
        referencesHandler.getMainController().getStartButton().setDisable(false);
        referencesHandler.getMainController().getStopMenuItem().setDisable(true);
        referencesHandler.getMainController().getStopButton().setDisable(true);

        simulationThread = null;
    }

    class SimulationThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted() && referencesHandler.getMainController().getMainStage().isShowing()) {
                try {
                    referencesHandler.getAutomaton().nextGeneration();
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    interrupt();
                } catch (Throwable e) {
                    e.printStackTrace();
                    AlertHelper.showError("Laufzeitfehler in der transform-Methode: " + e);
                }
            }
        }
    }
}