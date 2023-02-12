import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;

public class test extends Automaton {

    private static final int initNumberOfRows = 50;
    private static final int initNumberOfColumns = 50;
    private static final int numberOfStates = 10;
    private static final boolean mooreNeighborhood = true;
    private static final boolean initTorus = true;

    public test() {
        super(initNumberOfRows, initNumberOfColumns,
                numberOfStates, mooreNeighborhood,
                initTorus);
    }
    
    protected Cell transform(Cell cell, Cell[] neighbors) {
        return cell;
    }
}
