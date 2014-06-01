package controller.simulation;

import edu.uci.ics.jung.graph.MyGraph;
import model.MyEdge;
import model.MyVertex;
import model.factories.EdgeFactory;

/**
 * Created by miroslavbatchkarov on 19/05/2014.
 */
public class EdgeCreationCommand implements SimulationCommand {


    private MyGraph<MyVertex, MyEdge> graph;
    private MyVertex from, to;
    private EdgeFactory factory;

    public EdgeCreationCommand(EdgeFactory factory, MyGraph<MyVertex,
            MyEdge> graph, MyVertex from, MyVertex to) {
        this.factory = factory;
        this.graph = graph;
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute() {
	    synchronized (this.graph) {
            System.out.println("Creating from " + from + " to " + to);
            if (!graph.isNeighbor(from, to))
                this.graph.addEdge(factory.create(), from, to);
	    }
    }
}
