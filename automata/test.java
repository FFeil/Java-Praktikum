import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;

public class test extends Automaton {

    final private static int initNumberOfRows = 50;
    final private static int initNumberOfColumns = 50;
    final private static int numberOfStates = 2;
    final private static boolean mooreNeighborhood = true;
    final private static boolean initTorus = true;

    public test() {
        super(initNumberOfRows, initNumberOfColumns,
                numberOfStates, mooreNeighborhood,
                initTorus);
    }
    protected Cell transform(Cell cell, Cell[] neighbors) {
        return cell;
    }
}
