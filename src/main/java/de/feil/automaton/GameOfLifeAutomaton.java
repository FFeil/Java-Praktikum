package de.feil.automaton;

public class GameOfLifeAutomaton extends Automaton {
    public GameOfLifeAutomaton(int rows, int columns, boolean isTorus) {
        super(0, 0, 0, false, false);
        //TODO
    }

    public GameOfLifeAutomaton() {
        this(50, 50, true);
    }

    protected Cell transform(Cell cell, Cell[] neighbors) {
        //TODO
        return null;
    }
}