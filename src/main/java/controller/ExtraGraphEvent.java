package controller;

import edu.uci.ics.jung.graph.Graph;

/**
 * Created by miroslavbatchkarov on 26/01/2014.
 */
public abstract class ExtraGraphEvent<V, E>{

    protected Graph<V, E> source;
    protected ExtraEventTypes type;

    /**
     * Creates an instance with the specified {@code source} graph and {@code Type}
     * (vertex/edge addition/removal).
     */
    public ExtraGraphEvent(Graph<V, E> source, ExtraEventTypes type) {
        this.source = source;
        this.type = type;
    }

    /**
     * Extra types of graph events.
     */
    public static enum ExtraEventTypes {
        GRAPH_REPLACED
        //todo add here as needed
    }

    public static class GraphReplacedEvent<V, E> extends ExtraGraphEvent<V, E> {

        public GraphReplacedEvent(Graph<V, E> source) {
            super(source, ExtraEventTypes.GRAPH_REPLACED);
        }
    }

}
