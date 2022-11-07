package de.feil.automaton;

import java.util.Arrays;

public class GameOfLifeAutomaton extends Automaton {

    public GameOfLifeAutomaton(int rows, int columns, boolean isTorus) {
        super(rows, columns, 2, true, isTorus);
    }

    public GameOfLifeAutomaton() {
        this(50, 50, true);
    }

    protected Cell transform(Cell cell, Cell[] neighbors) {
        long numberOfActiveNeighbors = Arrays.stream(neighbors)
                .filter(c -> c.getState() == 1)
                .count();

        if ((numberOfActiveNeighbors == 2 && cell.getState() == 1) || numberOfActiveNeighbors == 3) {
            return new Cell(1);
        }

        return new Cell();
    }
}