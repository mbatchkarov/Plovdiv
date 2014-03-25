package controller;

import edu.uci.ics.jung.graph.Graph;

/**
 * Created by miroslavbatchkarov on 26/01/2014.
 */
public class ExtraGraphEvent<V, E> {

    public Graph<V, E> source;
    public int type;

    public static final int GRAPH_REPLACED = 0;
    public static final int SIM_STEP_COMPLETE = 1;
    public static final int STATS_CHANGED = 2;
    public static final int METADATA_CHANGED = 3;
    //todo add here as needed

    /**
     * Creates an instance with the specified {@code source} graph and {@code Type}
     * (vertex/edge addition/removal).
     */
    public ExtraGraphEvent(Graph<V, E> source, int type) {
        this.source = source;
        this.type = type;
    }
}
