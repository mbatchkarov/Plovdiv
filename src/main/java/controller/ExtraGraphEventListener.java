package controller;

/**
 * Created by miroslavbatchkarov on 26/01/2014.
 */
public interface ExtraGraphEventListener<V,E> {
    public void handleExtraGraphEvent(ExtraGraphEvent<V,E> evt);
}
