package de.feil;

import de.feil.automaton.Automaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PopulationPanel extends Region {

    private static double AUTOMATON_WIDTH = 15;
    private static double AUTOMATON_HEIGHT = 15;
    private static final double BORDER_WIDTH = 10;
    private static final double BORDER_HEIGHT = 10;

    private final Automaton automaton;
    private final Canvas canvas;

    private int selectedRadioButton;
    private final ArrayList<ColorPicker> colorPickers;

    private int rowDragStart;
    private int columnDragStart;


    public PopulationPanel(Automaton automaton, List<ColorPicker> colorPickers) {
        this.automaton = automaton;
        this.canvas = new Canvas(calcCanvasWidth(), calcCanvasHeight());
        this.getChildren().add(canvas);

        selectedRadioButton = 0;
        this.colorPickers = (ArrayList<ColorPicker>) colorPickers;

        this.canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, this::onMousePressed);
        this.canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::onMouseDragged);

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
        System.out.println(AUTOMATON_HEIGHT);
        if (AUTOMATON_WIDTH < 65 && AUTOMATON_HEIGHT < 65) {
            AUTOMATON_WIDTH += 2;
            AUTOMATON_HEIGHT += 2;

            resizeCanvas();

            return true;
        }

        return false;
    }

    public boolean zoomOut() {
        if (AUTOMATON_WIDTH > 3 && AUTOMATON_HEIGHT > 3) {
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
        resize(calcCanvasWidth(), calcCanvasHeight());

        paintCanvas();
    }

    private void onMousePressed(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (validateMouseCoordinates(x, y)) {
            return;
        }

        rowDragStart = getMouseRow(y);
        columnDragStart = getMouseColumn(x);

        automaton.setState(rowDragStart, columnDragStart, selectedRadioButton);
        paintCanvas();
    }

    private void onMouseDragged(MouseEvent event) {
        double x = event.getX();
        double y = event.getY();

        if (validateMouseCoordinates(x, y)) {
            return;
        }

        int currentRow = getMouseRow(y);
        int currentCol = getMouseColumn(x);

        // Variablen für die for-Schleife initiieren + setzen
        int rowStart;
        int rowEnd;
        int colStart;
        int colEnd;

        if (rowDragStart < currentRow) {
            rowStart = rowDragStart;
            rowEnd = currentRow;
        } else {
            rowStart = currentRow;
            rowEnd = rowDragStart;
        }
        if (columnDragStart < currentCol) {
            colStart = columnDragStart;
            colEnd = currentCol;
        } else {
            colStart = currentCol;
            colEnd = columnDragStart;
        }

        // Sonst Array out of bounce exception
        if (rowEnd == automaton.getNumberOfRows()) {
            rowEnd--;
        }
        if (colEnd == automaton.getNumberOfColumns()) {
            colEnd--;
        }

        // Zellen setzen
        for (int i = rowStart; i < rowEnd + 1; i++) {
            for (int j = colStart; j < colEnd + 1; j++) {
                automaton.setState(i, j, selectedRadioButton);
            }
        }

        paintCanvas();
    }

    private boolean validateMouseCoordinates(double x, double y) {
        return x < BORDER_WIDTH || y < BORDER_HEIGHT || x > BORDER_WIDTH + automaton.getNumberOfColumns() * AUTOMATON_WIDTH
                || y > BORDER_HEIGHT + automaton.getNumberOfRows() * AUTOMATON_HEIGHT;
    }

    private int getMouseRow(double y) {
        return (int) ((y - BORDER_HEIGHT) / AUTOMATON_HEIGHT);
    }

    private int getMouseColumn(double x) {
        return (int) ((x - BORDER_WIDTH) / AUTOMATON_WIDTH);
    }
}
