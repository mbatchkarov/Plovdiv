package model;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.event.GraphEvent;

/**
 * Created by miroslavbatchkarov on 25/01/2014.
 */
public class SimulationEvent<MyVertex, MyEdge> {
    /**
     * Creates an instance with the specified {@code source} graph and {@code Type}
     * (vertex/edge addition/removal).
     *
     * @param source
     * @param type
     */
    public SimulationEvent(Graph<MyVertex, MyEdge> source, Type type) {
    }

    /**
     * Types of graph events.
     */
    public static enum Type {
        VERTEX_SET_TO_INFECTED,
        VERTEX_SET_TO_HEALED,
        VERTED_SET_TO_RESISTANT
    }
}
