package saffi.dataevent;

import org.junit.Assert;
import org.junit.Test;

public class DataEventWordCounterTest {
    @Test
    public void testAdd() {
        DataEvent de = new DataEvent("type", "data", 1L);
        DataEventCounter counter = new DataEventCounter();
        Assert.assertEquals(new Integer(0),
                counter.getEventCount().get("type"));
        Assert.assertEquals(new Integer(0),
                counter.getWordCount().get("data"));
        counter.add(de);
        Assert.assertEquals(new Integer(1),
                counter.getEventCount().get("type"));
        Assert.assertEquals(new Integer(1),
                counter.getWordCount().get("data"));
    }
}
