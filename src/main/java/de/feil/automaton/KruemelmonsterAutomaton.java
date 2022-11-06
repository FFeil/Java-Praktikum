package de.feil.automaton;

import java.util.Arrays;
import java.util.Optional;

public class KruemelmonsterAutomaton extends Automaton {

    public KruemelmonsterAutomaton(int rows, int columns, boolean isTorus) {
        super(rows, columns, 10, false, isTorus);
    }

    public KruemelmonsterAutomaton() {
        this(100, 100, true);
    }

    protected Cell transform(Cell cell, Cell[] neighbors) {
        Optional<Cell> neighborCell = Arrays.stream(neighbors)
                .filter(c -> cell.getState() == c.getState() + 1 % getNumberOfStates())
                .findAny();

        return new Cell(neighborCell.orElse(new Cell(cell.getState())).getState());
    }
}

