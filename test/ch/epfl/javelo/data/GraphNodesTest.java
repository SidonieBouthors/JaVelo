package ch.epfl.javelo.data;

import ch.epfl.javelo.Q28_4;
import org.junit.jupiter.api.Test;

import java.nio.IntBuffer;

import static org.junit.jupiter.api.Assertions.*;

class GraphNodesTest {
    @Test
    void Test(){
        IntBuffer b = IntBuffer.wrap(new int[]{
                2_600_000 << 4,
                1_200_000 << 4,
                0x2_000_1234
        });
        GraphNodes ns = new GraphNodes(b);
        assertEquals(1, ns.count());
        assertEquals(2_600_000, ns.nodeE(0));
        assertEquals(1_200_000, ns.nodeN(0));
        assertEquals(2, ns.outDegree(0));
        assertEquals(0x1234, ns.edgeId(0, 0));
        assertEquals(0x1235, ns.edgeId(0, 1));
    }
    @Test
    void test2() {
        int nodeN=929392;
        int nodeE=6753745;
        int lastint= 1;

        IntBuffer a = IntBuffer.allocate(21);
        a.put(nodeE);
        a.put(nodeN);
        a.put(987392047);

        GraphNodes b = new GraphNodes(a);
        assertEquals(7,b.count());
        assertEquals(Q28_4.asDouble(nodeE), b.nodeE(0));
        assertEquals(Q28_4.asDouble(nodeN),b.nodeN(0));
        assertEquals(0,b.outDegree(0));
        assertEquals(1,b.edgeId(0,1));

    }

}