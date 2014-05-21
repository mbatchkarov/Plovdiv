package controller;

import controller.Controller;
import controller.Generator;
import controller.Stats;
import controller.simulation.SimulationCommand;
import edu.uci.ics.jung.graph.MyGraph;
import model.MyEdge;
import model.MyVertex;
import model.SimulationDynamics;
import model.factories.GraphFactory;
import org.apache.commons.math3.util.MathArrays;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class SimulatorTest {

    MyGraph<MyVertex, MyEdge> g;
    Controller cont;

    @Before
    public void setUp() throws Exception {
        MyGraph g = new GraphFactory().create();
        Stats stats = new Stats(g);
        cont = new Controller(stats, g);
        this.g = Generator.generateRectangularLattice(6, 6, cont, false);
    }

    /**
     * Test that increasing the time step causes more events to occur per step in this case there
     * is only one kind of event- rewiring of an SS edge
     */
    @Test
    public void testEventFrequencyInASingleStep() {
        assertEquals(g.getVertexCount(), 36);
        assertEquals(g.getEdgeCount(), 60);

        double[] timeSteps = new double[]{0.1, 0.3, 0.5, 0.7, 0.9, 1.1, 1.5, 2.};
        double[] eventCounts = new double[]{0., 0., 0., 0., 0., 0., 0., 0.};

        for (int i = 0; i < timeSteps.length; i++) {
            SimulationDynamics d = new SimulationDynamics(SimulationDynamics.DynamicsType.SIS,
                                                          1., 1., timeSteps[i],
                                                          0., 0., 0.,
                                                          0, 0., 1.,
                                                          0., 0., 0.);
            g.setDynamics(d);
            for (int j = 0; j < 50; j++) {
                LinkedList<SimulationCommand> events = new LinkedList<SimulationCommand>();
                cont.getSimulator().checkForTopologyChanges(g, events);
                cont.getSimulator().checkForInfection(g, events);
                cont.getSimulator().checkForRecovery(g, events);
                eventCounts[i] += events.size();
                assertEquals(g.getEdgeCount(), 60);
            }
        }
        System.out.println(Arrays.toString(eventCounts));
        MathArrays.checkOrder(eventCounts);
    }

}