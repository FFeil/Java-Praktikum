package de.feil;

import de.feil.automaton.Automaton;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

public class PopulationPanel extends Region {

    private static double AUTOMATON_WIDTH = 15;
    private static double AUTOMATON_HEIGHT = 15;
    private static final double BORDER_WIDTH = 10;
    private static final double BORDER_HEIGHT = 10;

    private final Automaton automaton;
    private final Canvas canvas;

    public PopulationPanel(Automaton automaton) {
        this.automaton = automaton;
        this.canvas = new Canvas(calcCanvasWidth(), calcCanvasHeight());
        this.getChildren().add(canvas);

        paintCanvas();
    }


    private void paintCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setLineWidth(.8);
        gc.setStroke(Color.GRAY);

        for (int i = 0; i < automaton.getNumberOfRows(); i++) {
            for (int j = 0; j < automaton.getNumberOfColumns(); j++) {
                if (automaton.getCell(i, j).getState() == 1) {
                    gc.setFill(Color.BLACK);
                } else {
                    gc.setFill(Color.WHITE);
                }

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

    public void zoomIn() {
        if (AUTOMATON_WIDTH < 134) {
            AUTOMATON_WIDTH += 2;
            AUTOMATON_HEIGHT += 2;

            canvas.setWidth(calcCanvasWidth());
            canvas.setHeight(calcCanvasHeight());
            paintCanvas();
        }
    }

    public void zoomOut() {
        if (AUTOMATON_HEIGHT > 3) {
            AUTOMATON_WIDTH -= 2;
            AUTOMATON_HEIGHT -= 2;

            canvas.setWidth(calcCanvasWidth());
            canvas.setHeight(calcCanvasHeight());
            paintCanvas();
        }
    }
}
