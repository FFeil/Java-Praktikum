package de.feil.view.panel;

import de.feil.controller.references.ReferenceHandler;
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

    private static int CELL_WIDTH = 15;
    private static int CELL_HEIGHT = 15;
    private static final double BORDER_WIDTH = 10;
    private static final double BORDER_HEIGHT = 10;

    private final ReferenceHandler referenceHandler;
    private final Canvas canvas;

    private final ArrayList<ColorPicker> colorPickers;

    public PopulationPanel(ReferenceHandler referenceHandler, List<ColorPicker> colorPickers) {
        this.referenceHandler = referenceHandler;
        this.canvas = new Canvas(calcCanvasWidth(), calcCanvasHeight());
        this.getChildren().add(canvas);
        this.colorPickers = (ArrayList<ColorPicker>) colorPickers;

        referenceHandler.setPopulationPanel(this);
        referenceHandler.getAutomaton().add(this);

        paintCanvas();
    }

    public static int getCellWidth() {
        return CELL_WIDTH;
    }

    public static int getCellHeight() {
        return CELL_HEIGHT;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCellSize(int height, int width) {
        CELL_HEIGHT = height;
        CELL_WIDTH = width;

        paintCanvas();
    }

    public void paintCanvas() {
        canvas.setWidth(calcCanvasWidth());
        canvas.setHeight(calcCanvasHeight());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setLineWidth(.8);
        gc.setStroke(Color.GRAY);

        for (int i = 0; i < referenceHandler.getAutomaton().getNumberOfRows(); i++) {
            for (int j = 0; j < referenceHandler.getAutomaton().getNumberOfColumns(); j++) {
                gc.setFill(colorPickers.get(referenceHandler.getAutomaton().getCell(i, j).getState()).getValue());
                gc.fillRect(BORDER_WIDTH + j * CELL_WIDTH, BORDER_HEIGHT + i * CELL_HEIGHT, CELL_WIDTH,
                        CELL_HEIGHT);
                gc.strokeRect(BORDER_WIDTH + j * CELL_WIDTH, BORDER_HEIGHT + i * CELL_HEIGHT, CELL_WIDTH,
                        CELL_HEIGHT);
            }
        }
    }

    public double calcCanvasWidth() {
        return 2 * BORDER_WIDTH + CELL_WIDTH * referenceHandler.getAutomaton().getNumberOfColumns();
    }

    public double calcCanvasHeight() {
        return 2 * BORDER_HEIGHT + CELL_HEIGHT * referenceHandler.getAutomaton().getNumberOfRows();
    }

    public boolean canZoomIn() {
        return CELL_WIDTH < 65 && CELL_HEIGHT < 65;
    }

    public void zoomIn() {
        CELL_WIDTH += 2;
        CELL_HEIGHT += 2;
    }

    public boolean canZoomOut() {
        return CELL_WIDTH > 3 && CELL_HEIGHT > 3;
    }

    public void zoomOut() {
        CELL_WIDTH -= 2;
        CELL_HEIGHT -= 2;
    }

    public Optional<Pair<Integer>> getRowAndCol(double x, double y) {
        if (x < BORDER_WIDTH || y < BORDER_HEIGHT
                || x > BORDER_WIDTH + referenceHandler.getAutomaton().getNumberOfColumns() * CELL_WIDTH
                || y > BORDER_HEIGHT + referenceHandler.getAutomaton().getNumberOfRows() * CELL_HEIGHT) {
            return Optional.empty();
        }

        int row = (int) ((y - BORDER_HEIGHT) / CELL_HEIGHT);
        int col = (int) ((x - BORDER_WIDTH) / CELL_WIDTH);

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
