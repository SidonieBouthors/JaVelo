package ch.epfl.javelo.data;

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

}