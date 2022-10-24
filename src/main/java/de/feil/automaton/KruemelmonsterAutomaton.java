package de.feil.automaton;

public class KruemelmonsterAutomaton extends Automaton {
    public KruemelmonsterAutomaton(int rows, int columns, boolean isTorus) {
        super(0, 0, 0, false, false);
        //TODO
    }

    public KruemelmonsterAutomaton() {
        this(100, 100, true);
    }

    protected Cell transform(Cell cell, Cell[] neighbors) {
        //TODO
        return null;
    }
}

