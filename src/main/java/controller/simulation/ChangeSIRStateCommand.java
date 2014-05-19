package controller.simulation;

import model.EpiState;
import model.MyVertex;

/**
 * Created by miroslavbatchkarov on 19/05/2014.
 */
public class ChangeSIRStateCommand implements SimulationCommand {
    private MyVertex v;
    private EpiState nextState;

    public ChangeSIRStateCommand(MyVertex v, EpiState nextState) {
        this.v = v;
        this.nextState = nextState;
    }

    @Override
    public void execute() {
        this.v.setEpiState(nextState);
    }
}
