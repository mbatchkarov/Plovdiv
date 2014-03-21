package controller;

import edu.uci.ics.jung.graph.Graph;

/**
 * Created by miroslavbatchkarov on 26/01/2014.
 */
public abstract class ExtraGraphEvent<V, E>{

    public Graph<V, E> source;
    public ExtraEventTypes type;

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
        GRAPH_REPLACED,
        SIM_STEP_COMPLETE,
        STATS_CHANGED,
        METADATA_CHANGED
        //todo add here as needed
    }

    public static class GraphReplacedEvent<V, E> extends ExtraGraphEvent<V, E> {

        public GraphReplacedEvent(Graph<V, E> source) {
            super(source, ExtraEventTypes.GRAPH_REPLACED);
        }
    }

    public static class GraphMetadataChangedEvent<V, E> extends ExtraGraphEvent<V, E> {

        public GraphMetadataChangedEvent(Graph<V, E> source) {
            super(source, ExtraEventTypes.METADATA_CHANGED);
        }
    }
    public static class SimStepCompleteEvent<V, E> extends ExtraGraphEvent<V, E> {

        public SimStepCompleteEvent(Graph<V, E> source) {
            super(source, ExtraEventTypes.SIM_STEP_COMPLETE);
        }
    }

    public static class StatsChangedEvent<V, E> extends ExtraGraphEvent<V, E> {

        public StatsChangedEvent(Graph<V, E> source) {
            super(source, ExtraEventTypes.STATS_CHANGED);
        }
    }

}
