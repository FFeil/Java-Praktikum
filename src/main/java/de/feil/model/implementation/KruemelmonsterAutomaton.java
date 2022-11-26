package de.feil.model.implementation;

import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;

import java.util.Arrays;
import java.util.Optional;

public class KruemelmonsterAutomaton extends Automaton {

    public KruemelmonsterAutomaton(int rows, int columns, int numberOfStates, boolean isTorus) {
        super(rows, columns, numberOfStates, false, isTorus);
    }

    public KruemelmonsterAutomaton() {
        this(100, 100, 10, true);
    }

    protected Cell transform(Cell cell, Cell[] neighbors) {
        Optional<Cell> neighborCell = Arrays.stream(neighbors)
                .filter(c -> cell.getState() == c.getState() + 1 % getNumberOfStates())
                .findAny();

        return new Cell(neighborCell.orElse(new Cell(cell.getState())).getState());
    }
}

