package controller.simulation;

import edu.uci.ics.jung.graph.MyGraph;
import model.*;

/**
 * Created by miroslavbatchkarov on 19/05/2014.
 */
public class EdgeBreakingCommand implements SimulationCommand {

    private MyGraph<MyVertex, MyEdge> graph;
    private MyEdge edge;

    public EdgeBreakingCommand(MyGraph<MyVertex, MyEdge> graph, MyEdge edge) {
        this.graph = graph;
        this.edge = edge;
    }

    @Override
    public void execute() {
        this.graph.removeEdge(edge);
    }
}
