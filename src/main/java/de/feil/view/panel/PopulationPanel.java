package de.feil.view.panel;

import de.feil.controller.references.ReferencesHandler;
import de.feil.util.Observer;
import de.feil.util.Pair;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Observe: Automaton
public class PopulationPanel extends Region implements Observer {

    private static double AUTOMATON_WIDTH = 15;
    private static double AUTOMATON_HEIGHT = 15;
    private static final double BORDER_WIDTH = 10;
    private static final double BORDER_HEIGHT = 10;

    private final ReferencesHandler referencesHandler;
    private final Canvas canvas;

    private final ArrayList<ColorPicker> colorPickers;

    public PopulationPanel(ReferencesHandler referencesHandler, List<ColorPicker> colorPickers) {
        this.referencesHandler = referencesHandler;
        this.canvas = new Canvas(calcCanvasWidth(), calcCanvasHeight());
        this.getChildren().add(canvas);
        this.colorPickers = (ArrayList<ColorPicker>) colorPickers;

        referencesHandler.setPopulationPanel(this);
        referencesHandler.getAutomaton().add(this);

        paintCanvas();
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void paintCanvas() {
        canvas.setWidth(calcCanvasWidth());
        canvas.setHeight(calcCanvasHeight());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setLineWidth(.8);
        gc.setStroke(Color.GRAY);

        for (int i = 0; i < referencesHandler.getAutomaton().getNumberOfRows(); i++) {
            for (int j = 0; j < referencesHandler.getAutomaton().getNumberOfColumns(); j++) {
                gc.setFill(colorPickers.get(referencesHandler.getAutomaton().getCell(i, j).getState()).getValue());
                gc.fillRect(BORDER_WIDTH + j * AUTOMATON_WIDTH, BORDER_HEIGHT + i * AUTOMATON_HEIGHT, AUTOMATON_WIDTH,
                        AUTOMATON_HEIGHT);
                gc.strokeRect(BORDER_WIDTH + j * AUTOMATON_WIDTH, BORDER_HEIGHT + i * AUTOMATON_HEIGHT, AUTOMATON_WIDTH,
                        AUTOMATON_HEIGHT);
            }
        }
    }

    public double calcCanvasWidth() {
        return 2 * BORDER_WIDTH + AUTOMATON_WIDTH * referencesHandler.getAutomaton().getNumberOfColumns();
    }

    public double calcCanvasHeight() {
        return 2 * BORDER_HEIGHT + AUTOMATON_HEIGHT * referencesHandler.getAutomaton().getNumberOfRows();
    }

    public boolean canZoomIn() {
        return AUTOMATON_WIDTH < 65 && AUTOMATON_HEIGHT < 65;
    }

    public void zoomIn() {
        AUTOMATON_WIDTH += 2;
        AUTOMATON_HEIGHT += 2;
    }

    public boolean canZoomOut() {
        return AUTOMATON_WIDTH > 3 && AUTOMATON_HEIGHT > 3;
    }

    public void zoomOut() {
        AUTOMATON_WIDTH -= 2;
        AUTOMATON_HEIGHT -= 2;
    }

    public Optional<Pair<Integer>> getRowAndCol(double x, double y) {
        if (x < BORDER_WIDTH || y < BORDER_HEIGHT
                || x > BORDER_WIDTH + referencesHandler.getAutomaton().getNumberOfColumns() * AUTOMATON_WIDTH
                || y > BORDER_HEIGHT + referencesHandler.getAutomaton().getNumberOfRows() * AUTOMATON_HEIGHT) {
            return Optional.empty();
        }

        int row = (int) ((y - BORDER_HEIGHT) / AUTOMATON_HEIGHT);
        int col = (int) ((x - BORDER_WIDTH) / AUTOMATON_WIDTH);

        return Optional.of(new Pair<>(row, col));
    }

    @Override
    public void update() {
        if (Platform.isFxApplicationThread()) {
            paintCanvas();
        } else {
            Platform.runLater(this::paintCanvas);
        }
    }
}
