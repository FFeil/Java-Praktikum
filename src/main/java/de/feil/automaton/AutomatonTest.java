package de.feil.automaton;

class AutomatonTest {

    static GameOfLifeAutomaton automaton = new GameOfLifeAutomaton(6, 6, false);

    public static void main(String[] args) throws Throwable {
        automaton.setState(1, 0, 1);
        automaton.setState(1, 1, 1);
        automaton.setState(1, 2, 1);

        printCells();

        automaton.nextGeneration();
        printCells();

        automaton.nextGeneration();
        printCells();

        automaton.nextGeneration();
        printCells();

        automaton.nextGeneration();
        printCells();
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
}
