package ch.epfl.javelo.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeSetTest {

    @Test
    void attributetest() {
        long actual = AttributeSet.of(Attribute.NCN_YES, Attribute.RCN_YES, Attribute.LCN_YES).bits();
        long expected = 7;
        assertEquals(expected, actual);

    }
    @Test
    void attributeContains(){
        AttributeSet actual = AttributeSet.of(Attribute.HIGHWAY_SERVICE, Attribute.BICYCLE_NO, Attribute.CYCLEWAY_OPPOSITE_LANE);
        assertTrue (actual.contains(Attribute.BICYCLE_NO));
    }
    @Test
    void testToString(){
        String actual = AttributeSet.of(Attribute.HIGHWAY_SERVICE, Attribute.BICYCLE_NO, Attribute.CYCLEWAY_OPPOSITE_LANE).toString();
        assertEquals("{highway=service,cycleway=opposite_lane,bicycle=no}", actual);
    }


}