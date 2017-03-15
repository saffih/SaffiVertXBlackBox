package saffi.dataevent;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by saffi on 15/03/17.
 */
public class DataEventTest {
    @Test
    public void equalsTest() throws Exception {
        final DataEvent a = new DataEvent("a", "b", 0);
        final DataEvent a2 = new DataEvent("a", "b", 0);
        final DataEvent ba = new DataEvent("a", "a", 0);
        final DataEvent bn = new DataEvent("a", "b", 1);
        assertEquals(a, a2);
        assertNotEquals(a, ba);
        assertNotEquals(a, bn);
    }

    @Test
    public void hashCodeTest() throws Exception {
        final DataEvent a = new DataEvent("a", "b", 0);
        final DataEvent a2 = new DataEvent("a", "b", 0);
        final DataEvent ba = new DataEvent("a", "a", 0);
        final DataEvent bn = new DataEvent("a", "b", 1);
        assertEquals(a.hashCode(), a2.hashCode());
        assertNotEquals(a.hashCode(), ba.hashCode());
        assertNotEquals(a.hashCode(), bn.hashCode());

    }

}