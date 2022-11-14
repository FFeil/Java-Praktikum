package de.feil;

import de.feil.automaton.Automaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class PopulationPanel extends Region {

    private static double AUTOMATON_WIDTH = 15;
    private static double AUTOMATON_HEIGHT = 15;
    private static final double BORDER_WIDTH = 10;
    private static final double BORDER_HEIGHT = 10;

    private final Automaton automaton;
    private final Canvas canvas;

    private int selectedRadioButton;
    private final ArrayList<ColorPicker> colorPickers;

    public PopulationPanel(Automaton automaton, ArrayList<ColorPicker> colorPickers) {
        this.automaton = automaton;
        this.canvas = new Canvas(calcCanvasWidth(), calcCanvasHeight());
        selectedRadioButton = 0;
        this.colorPickers = colorPickers;

        this.canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);

        this.canvas.addEventHandler(MouseEvent.DRAG_DETECTED, this::onMouseDragDetected);
        this.canvas.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, this::onMouseDragReleased);
        this.canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);

        this.getChildren().add(canvas);

        paintCanvas();
    }

    public void setSelectedRadioButton(int selectedRadioButton) {
        this.selectedRadioButton = selectedRadioButton;
    }

    public void paintCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setLineWidth(.8);
        gc.setStroke(Color.GRAY);

        for (int i = 0; i < automaton.getNumberOfRows(); i++) {
            for (int j = 0; j < automaton.getNumberOfColumns(); j++) {
                gc.setFill(colorPickers.get(automaton.getCell(i, j).getState()).getValue());
                gc.fillRect(BORDER_WIDTH + j * AUTOMATON_WIDTH, BORDER_HEIGHT + i * AUTOMATON_HEIGHT, AUTOMATON_WIDTH,
                        AUTOMATON_HEIGHT);
                gc.strokeRect(BORDER_WIDTH + j * AUTOMATON_HEIGHT, BORDER_HEIGHT + i * AUTOMATON_HEIGHT, AUTOMATON_WIDTH,
                        AUTOMATON_HEIGHT);
            }
        }
    }

    public double calcCanvasWidth() {
        return 2 * BORDER_WIDTH + AUTOMATON_WIDTH * automaton.getNumberOfColumns();
    }

    public double calcCanvasHeight() {
        return 2 * BORDER_HEIGHT + AUTOMATON_HEIGHT * automaton.getNumberOfRows();
    }

    public boolean zoomIn() {
        if (AUTOMATON_WIDTH < 134) {
            AUTOMATON_WIDTH += 2;
            AUTOMATON_HEIGHT += 2;

            resizeCanvas();

            return true;
        }

        return false;
    }

    public boolean zoomOut() {
        if (AUTOMATON_HEIGHT > 3) {
            AUTOMATON_WIDTH -= 2;
            AUTOMATON_HEIGHT -= 2;

            resizeCanvas();

            return true;
        }

        return false;
    }

    public void resizeCanvas() {
        canvas.setWidth(calcCanvasWidth());
        canvas.setHeight(calcCanvasHeight());
        paintCanvas();

        resize(calcCanvasWidth(), calcCanvasHeight());
    }

    private void onMouseClicked(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (x < BORDER_WIDTH || y < BORDER_HEIGHT || x > BORDER_WIDTH + automaton.getNumberOfColumns() * AUTOMATON_WIDTH
                || y > BORDER_HEIGHT + automaton.getNumberOfRows() * AUTOMATON_HEIGHT) {
            return;
        }

        int row = (int) ((y - BORDER_HEIGHT) / AUTOMATON_HEIGHT);
        int col = (int) ((x - BORDER_WIDTH) / AUTOMATON_WIDTH);

        automaton.setState(row, col, selectedRadioButton);
        paintCanvas();
    }
    private int rowDragStart;
    private int columnDragStart;

    private void onMouseDragDetected(MouseEvent event) {
        startFullDrag();
        double x = event.getX();
        double y = event.getY();

        if (x < BORDER_WIDTH || y < BORDER_HEIGHT || x > BORDER_WIDTH + automaton.getNumberOfColumns() * AUTOMATON_WIDTH
                || y > BORDER_HEIGHT + automaton.getNumberOfRows() * AUTOMATON_HEIGHT) {
            return;
        }

        rowDragStart = (int) ((y - BORDER_HEIGHT) / AUTOMATON_HEIGHT);
        columnDragStart = (int) ((x - BORDER_WIDTH) / AUTOMATON_WIDTH);
    }

    private void onMouseDragged(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (x < BORDER_WIDTH || y < BORDER_HEIGHT || x > BORDER_WIDTH + automaton.getNumberOfColumns() * AUTOMATON_WIDTH
                || y > BORDER_HEIGHT + automaton.getNumberOfRows() * AUTOMATON_HEIGHT) {
            return;
        }

        int row = (int) ((y - BORDER_HEIGHT) / AUTOMATON_HEIGHT);
        int col = (int) ((x - BORDER_WIDTH) / AUTOMATON_WIDTH);


        for (int i = rowDragStart; i < row + 1; i++) {
            for (int j = columnDragStart; j < col + 1; j++) {
                automaton.setState(i, j, selectedRadioButton);
            }
        }

        paintCanvas();
    }


    private void onMouseDragReleased(MouseDragEvent event) {

    }
}
