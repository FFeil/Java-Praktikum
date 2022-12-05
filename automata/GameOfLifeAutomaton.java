import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;

import java.util.Arrays;

public class GameOfLifeAutomaton extends Automaton {

    public GameOfLifeAutomaton(int rows, int columns, boolean isTorus) {
        super(rows, columns, 2, true, isTorus);
    }

    public GameOfLifeAutomaton() {
        this(50, 50, true);
    }

    protected synchronized Cell transform(Cell cell, Cell[] neighbors) {
        long numberOfActiveNeighbors = Arrays.stream(neighbors)
                .filter(neighborCell -> neighborCell.getState() == 1)
                .count();

        if ((numberOfActiveNeighbors == 2 && cell.getState() == 1) || numberOfActiveNeighbors == 3) {
            return new Cell(1);
        }

        return new Cell();
    }
}