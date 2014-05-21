package controller.simulation;

/**
 * A base classe for all Command-like simulation events. These include topology changes and
 * changes to the epidemiologic state (e.g. recovery, infection, etc)
 * Created by miroslavbatchkarov on 19/05/2014.
 */
public interface SimulationCommand {

    public void execute();
}
