/*
 * Copyright (c) 2009, Miroslav Batchkarov, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products  derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package controller;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.map.LazyMap;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.layout.ObservableCachingLayout;
import edu.uci.ics.jung.visualization.layout.PersistentLayout;
import edu.uci.ics.jung.visualization.util.Caching;
import edu.uci.ics.jung.visualization.util.ChangeEventSupport;
import java.io.File;
import java.io.InputStream;


/**
 * Implementation of PersistentLayout.
 * Defers to another layout until 'restore' is called,
 * then it uses the saved vertex locations
 *
 * @param <V>
 * @param <E>
 * @author Tom Nelson
 *
 *
 */
public class PersistentLayoutImpl2<V, E> extends ObservableCachingLayout<V,E>
    implements PersistentLayout<V,E>,  ChangeEventSupport, Caching {

    /**
     * a container for Vertices
     */
    protected Map<V,Point> map;

    /**
     * a collection of Vertices that should not move
     */
    protected Set<V> dontmove;

    /**
     * whether the graph is locked (stops the VisualizationViewer rendering thread)
     */
    protected boolean locked;

    /**
     * create an instance with a passed layout
     * create containers for graph components
     * @param layout
     */
    public PersistentLayoutImpl2(Layout<V,E> layout) {
        super(layout);
        this.map = LazyMap.decorate(new HashMap<V,Point>(), new RandomPointFactory(getSize()));

        this.dontmove = new HashSet<V>();
    }

    /**
     * This method calls <tt>initialize_local_vertex</tt> for each vertex, and
     * also adds initial coordinate information for each vertex. (The vertex's
     * initial location is set by calling <tt>initializeLocation</tt>.
     */
    protected void initializeLocations() {
        for(V v : getGraph().getVertices()) {
            Point2D coord = delegate.transform(v);
            if (!dontmove.contains(v))
                initializeLocation(v, coord, getSize());
        }
    }


    /**
     * Sets persisted location for a vertex within the dimensions of the space.
     * If the vertex has not been persisted, sets a random location. If you want
     * to initialize in some different way, override this method.
     *
     * @param v
     * @param coord
     * @param d
     */
    protected void initializeLocation(V v, Point2D coord, Dimension d) {

        Point point = map.get(v);
        coord.setLocation(point.x, point.y);
    }

    /**
     * save the Vertex locations to a file
     * @param fileName the file to save to
     * @throws an IOException if the file cannot be used
     */
    public void persist(String fileName) throws IOException {

        for(V v : getGraph().getVertices()) {
            Point p = new Point(transform(v));
            map.put(v, p);
        }
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                fileName));
        oos.writeObject(map);
        oos.close();
    }

    /**
     * Restore the graph Vertex locations from a file
     * @param fileName the file to use
     * @throws IOException for file problems
     * @throws ClassNotFoundException for classpath problems
     */
    @SuppressWarnings("unchecked")
	public void restore(String fileName) throws IOException,
            ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                fileName));
        map = (Map) ois.readObject();
        ois.close();
        initializeLocations();
        locked = true;
        fireStateChanged();
    }

     /**
     * Restore the graph Vertex locations from a file
      * @throws IOException for file problems
     * @throws ClassNotFoundException for classpath problems
     */
	public void restore(InputStream is) throws IOException,
            ClassNotFoundException {
//        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
//                f));
            ObjectInputStream ois = new ObjectInputStream(is);
        map = (Map) ois.readObject();
        ois.close();
        initializeLocations();
        locked = true;
        fireStateChanged();
    }

    public void lock(boolean locked) {
        this.locked = locked;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.uci.ics.jung.visualization.Layout#incrementsAreDone()
     */
    public boolean done() {
        return super.done() || locked;
    }

    /*
     * (non-Javadoc)
     *
     * @see edu.uci.ics.jung.visualization.Layout#lockVertex(edu.uci.ics.jung.graph.Vertex)
     */
    public void lock(V v, boolean state) {
        dontmove.add(v);
        delegate.lock(v, state);
    }

    @SuppressWarnings("serial")
	public static class RandomPointFactory implements Factory<Point>, Serializable {

    	Dimension d;
    	public RandomPointFactory(Dimension d) {
    		this.d = d;
    	}
		public edu.uci.ics.jung.visualization.layout.PersistentLayout.Point create() {
	            double x = Math.random() * d.width;
	            double y = Math.random() * d.height;
				return new Point(x,y);
		}
    }

}