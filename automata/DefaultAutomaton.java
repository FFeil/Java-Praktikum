import de.feil.model.base.Automaton;
import de.feil.model.base.Cell;

public class DefaultAutomaton extends Automatn {

    private static final int initNumberOfRows = 50;
    private static final int initNumberOfColumns = 50;
    private static final int numberOfStates = 2;
    private static final boolean mooreNeighborhood = true;
    private static final boolean initTorus = true;

    public DefaultAutomaton() {
        super(initNumberOfRows, initNumberOfColumns,
                numberOfStates, mooreNeighborhood,
                initTorus);
    }
    
    protected Cell transform(Cell cell, Cell[] neighbors) {
        return cell;
    }
}
