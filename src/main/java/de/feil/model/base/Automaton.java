package de.feil.model.base;

import de.feil.util.Observable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Abstrakte Klasse zur Repräsentation eines zellulären Automaten
 */
public abstract class Automaton extends Observable {

    private int numberOfRows;
    private int numberOfColumns;
    private final int numberOfStates;
    private final boolean isMooreNeighborHood;
    private boolean isTorus;
    private Cell[][] cells;
    private final Random random;

    /**
     * Konstruktor
     *
     * @param rows                Anzahl an Reihen
     * @param columns             Anzahl an Spalten
     * @param numberOfStates      Anzahl an Zuständen; die Zustände
     *                            des Automaten
     *                            sind dann die Werte 0 bis
     *                            numberOfStates-1
     * @param isMooreNeighborHood true, falls der Automat die
     *                            Moore-Nachbarschaft
     *                            benutzt; false, falls der Automat die
     *                            von-Neumann-Nachbarschaft benutzt
     * @param isTorus             true, falls die Zellen als
     *                            Torus betrachtet werden
     */
    protected Automaton(int rows, int columns, int numberOfStates,
                     boolean isMooreNeighborHood, boolean isTorus) {
        numberOfRows = rows;
        numberOfColumns = columns;
        this.numberOfStates = numberOfStates;
        this.isMooreNeighborHood = isMooreNeighborHood;
        this.isTorus = isTorus;
        cells = new Cell[rows][columns];
        random = new Random();

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    /**
     * Implementierung der Transformationsregel
     *
     * @param cell      die betroffene Zelle (darf nicht verändert
     *                  werden!!!)
     * @param neighbors die Nachbarn der betroffenen Zelle (dürfen nicht
     *                  verändert werden!!!)
     * @return eine neu erzeugte Zelle, die gemäß der
     * Transformationsregel aus der
     * betroffenen Zelle hervorgeht
     * @throws Throwable moeglicherweise wirft die Methode eine Exception
     */
    protected abstract Cell transform(Cell cell, Cell[] neighbors)
            throws Throwable;

    /**
     * Liefert die Anzahl an Zuständen des Automaten; gültige Zustände sind
     * int-Werte zwischen 0 und Anzahl-1
     *
     * @return die Anzahl an Zuständen des Automaten
     */
    public synchronized int getNumberOfStates() {
        return numberOfStates;
    }

    /**
     * Liefert die Anzahl an Reihen
     *
     * @return die Anzahl an Reihen
     */
    public synchronized int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Liefert die Anzahl an Spalten
     *
     * @return die Anzahl an Spalten
     */
    public synchronized int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * Ändert die Größe des Automaten; Achtung: aktuelle Belegungen nicht
     * gelöschter Zellen sollen beibehalten werden; neue Zellen sollen im
     * Zustand 0 erzeugt werden
     *
     * @param rows    die neue Anzahl an Reihen
     * @param columns die neue Anzahl an Spalten
     */
    public synchronized void changeSize(int rows, int columns) {
        Cell[][] newCells = new Cell[rows][columns];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (i < numberOfRows && j < numberOfColumns) {
                    newCells[i][j] = cells[i][j];
                } else {
                    newCells[i][j] = new Cell();
                }
            }
        }

        numberOfRows = rows;
        numberOfColumns = columns;
        cells = newCells;

        notifyObserver();
    }

    /**
     * Liefert Informationen, ob der Automat als Torus betrachtet wird
     *
     * @return true, falls der Automat als Torus betrachtet wird; false
     * sonst
     */
    public synchronized boolean isTorus() {
        return isTorus;
    }

    /**
     * Ändert die Torus-Eigenschaft des Automaten
     *
     * @param isTorus true, falls der Automat als Torus betrachtet wird;
     *                false sonst
     */
    public synchronized void setTorus(boolean isTorus) {
        this.isTorus = isTorus;

        notifyObserver();
    }

    /**
     * Liefert Informationen über die Nachbarschaft-Eigenschaft des
     * Automaten
     * (Hinweis: Die Nachbarschaftseigenschaft kann nicht verändert werden)
     *
     * @return true, falls der Automat die Moore-Nachbarschaft berücksicht;
     * false, falls er die von-Neumann-Nachbarschaft berücksichtigt
     */
    public synchronized boolean isMooreNeighborHood() {
        return isMooreNeighborHood;
    }

    /**
     * setzt alle Zellen in den Zustand 0
     */
    public synchronized void clearPopulation() {
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                cells[i][j].setState(0);
            }
        }

        notifyObserver();
    }

    /**
     * setzt für jede Zelle einen zufällig erzeugten Zustand
     */
    public synchronized void randomPopulation() {
        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                cells[i][j].setState(random.nextInt(numberOfStates));
            }
        }

        notifyObserver();
    }

    /**
     * Liefert eine Zelle des Automaten
     *
     * @param row    Reihe der Zelle
     * @param column Spalte der Zelle
     * @return Cell-Objekt a Position row/column
     */
    public synchronized Cell getCell(int row, int column) {
        return cells[row][column];
    }

    /**
     * Aendert den Zustand einer Zelle
     *
     * @param row    Reihe der Zelle
     * @param column Spalte der Zelle
     * @param state  neuer Zustand der Zelle
     */
    public synchronized void setState(int row, int column, int state) {
        cells[row][column].setState(state);

        notifyObserver();
    }

    /**
     * Aendert den Zustand eines ganzen Bereichs von Zellen
     *
     * @param fromRow    Reihe der obersten Zelle
     * @param fromColumn Spalte der obersten Zelle
     * @param toRow      Reihe der untersten Zelle
     * @param toColumn   Spalte der untersten Zelle
     * @param state      neuer Zustand der Zellen
     */
    public synchronized void setState(int fromRow, int fromColumn, int toRow,
                         int toColumn, int state) {
        for (int i = fromRow; i < toRow; i++) {
            for (int j = fromColumn; j < toColumn; j++) {
                cells[i][j].setState(state);
            }
        }

        notifyObserver();
    }

    /**
     * überführt den Automaten in die nächste Generation; ruft dabei die
     * abstrakte Methode "transform" für alle Zellen auf; Hinweis: zu
     * berücksichtigen sind die Nachbarschaftseigenschaft und die
     * Torus-Eigenschaft des Automaten
     *
     * @throws Throwable Exceptions der transform-Methode werden
     *                   weitergeleitet
     */
    public synchronized void nextGeneration() throws Throwable {
        Cell[][] newCells = new Cell[numberOfRows][numberOfColumns];

        for (int i = 0; i < numberOfRows; i++) {
            for (int j = 0; j < numberOfColumns; j++) {
                newCells[i][j] = transform(cells[i][j], getNeighbors(i, j));
            }
        }

        cells = newCells;

        notifyObserver();
    }

    /**
     * Gibt alle Nachbarn einer Zelle zurück
     *
     * @param row    Reihe der Zelle
     * @param column Spalte der Zelle
     * @return Alle Nachbarn als Cell-Array
     */
    private Cell[] getNeighbors(int row, int column) {
        if (isMooreNeighborHood) {
            return getMooreNeighbors(row, column);
        } else {
            return getNeumannNeighbors(row, column);
        }
    }

    /**
     * Gibt alle Moore-Nachbarn einer Zelle zurück
     *
     * @param row    Reihe der Zelle
     * @param column Spalte der Zelle
     * @return Alle Moore-Nachbarn als Cell-Array
     */
    private Cell[] getMooreNeighbors(int row, int column) {
        ArrayList<Cell> neighbors = new ArrayList<>();

        addNeighbor(neighbors, row - 1, column - 1);
        addNeighbor(neighbors, row - 1, column);
        addNeighbor(neighbors, row - 1, column + 1);
        addNeighbor(neighbors, row, column - 1);
        addNeighbor(neighbors, row, column);
        addNeighbor(neighbors, row, column + 1);
        addNeighbor(neighbors, row + 1, column - 1);
        addNeighbor(neighbors, row + 1, column);
        addNeighbor(neighbors, row + 1, column + 1);

        return neighbors.toArray(new Cell[neighbors.size()]);
    }


    /**
     * Gibt alle Neumann-Nachbarn einer Zelle zurück
     *
     * @param row    Reihe der Zelle
     * @param column Spalte der Zelle
     * @return Alle Neumann-Nachbarn als Cell-Array
     */
    private Cell[] getNeumannNeighbors(int row, int column) {
        ArrayList<Cell> neighbors = new ArrayList<>();

        addNeighbor(neighbors, row - 1, column);
        addNeighbor(neighbors, row + 1, column);

        addNeighbor(neighbors, row, column - 1);
        addNeighbor(neighbors, row, column + 1);

        return neighbors.toArray(new Cell[neighbors.size()]);
    }


    /**
     * Fügt eine Zelle zu einer Liste hinzu, falls entweder die Dimensionen im
     * Rahmen von cells sind oder der Torus true ist.
     *
     * @param i Reihe der Zelle
     * @param j Spalte der Zelle
     */
    private void addNeighbor(ArrayList<Cell> container, int i, int j) {
        int tmpI = i;
        int tmpJ = j;

        // Positives Modulo
        tmpI = (tmpI + numberOfRows) % numberOfRows;
        tmpJ = (tmpJ + numberOfColumns) % numberOfColumns;

        // (i == tmpI && j == tmpJ) => Index außerhalb von cells, falls torus == false
        if (isTorus || (i == tmpI && j == tmpJ)) {
            container.add(cells[tmpI][tmpJ]);
        }

        notifyObserver();
    }

    /**
     * Liefert alle Zellen des Automaten
     *
     * @return Cell-Array Objekt
     */
    public synchronized Cell[][] getCells() {
        return cells;
    }

    @Override
    public synchronized boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        if (this.numberOfRows == ((Automaton) obj).numberOfRows
                && this.numberOfColumns == ((Automaton) obj).numberOfColumns
                && this.numberOfStates == ((Automaton) obj).numberOfStates
                && this.isMooreNeighborHood == ((Automaton) obj).isMooreNeighborHood
                && this.isTorus == ((Automaton) obj).isTorus) {
            for (int i = 0; i < numberOfRows; i++) {
                for (int j = 0; j < numberOfColumns; j++) {
                   if (this.cells[i][j].getState() != ((Automaton) obj).cells[i][j].getState()) {
                       return false;
                   }
                }
            }
            return true;
        }
        return false;
    }
}