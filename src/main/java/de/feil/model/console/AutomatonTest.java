package de.feil.model.console;

import de.feil.model.base.Cell;
import de.feil.model.implementation.GameOfLifeAutomaton;

class AutomatonTest {

    static GameOfLifeAutomaton automaton = new GameOfLifeAutomaton(6, 6, false);

    public static void main(String[] args) throws Throwable {
        //blinkerTest();
        uhrTest();
    }

    private static void printCells() {
        Cell[][] cells = automaton.getCells();
        int numberOfRows = automaton.getNumberOfRows();
        int numberOfColumns = automaton.getNumberOfColumns();

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                System.out.print(cells[i][j].getState() + " ");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println();
    }

    private static void blinkerTest() throws Throwable {
        automaton.clearPopulation();

        automaton.setState(0, 0, 1);
        automaton.setState(0, 1, 1);
        automaton.setState(0, 2, 1);

        System.out.println("Generation 0");
        printCells();

        for (int i = 1; i < 4; i++) {
            automaton.nextGeneration();

            System.out.println("Generation " + i);
            printCells();
        }
    }

    private static void uhrTest() throws Throwable {
        automaton.clearPopulation();

        automaton.setState(2, 2, 1);
        automaton.setState(2, 3, 1);
        automaton.setState(1, 4, 1);
        automaton.setState(3, 4, 1);
        automaton.setState(3, 5, 1);
        automaton.setState(4, 3, 1);

        System.out.println("Generation 0");
        printCells();

        for (int i = 1; i < 4; i++) {
            automaton.nextGeneration();

            System.out.println("Generation " + i);
            printCells();
        }
    }
}
