package controller;

import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.io.CustomPajekNetReader;
import model.factories.GraphFactory;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * Test if both regular Pajek files and our extensions to it can be read in
 * Created by miroslavbatchkarov on 12/12/2015.
 */
public class CustomPajekNetReaderTest {

    @Test
    public void testLoadStandartPajekFormat() throws IOException {
        String[] paths = new String[]{"/cayley.pajek.txt", "/cayley.ours.txt"};
        for (String path : paths) {
            MyGraph g = getGraphFromPath(path);
            assertEquals(81, g.getVertexCount());
            assertEquals(42, g.getEdgeCount());
        }

        MyGraph g = getGraphFromPath("/malformed.pajek.txt");
        assertEquals(81, g.getVertexCount());
        assertEquals(41, g.getEdgeCount()); // one less edge
    }

    private MyGraph getGraphFromPath(String path) throws IOException {
        MyGraph g = new GraphFactory().create();
        Controller c = new Controller(null, g);
        CustomPajekNetReader reader = new CustomPajekNetReader(c.getVertexFactory(), c.getEdgeFactory());
        InputStream is = getClass().getResourceAsStream(path);
        g = (MyGraph) reader.load(new InputStreamReader(is), c.getGraphFactory());
        return g;
    }
}
