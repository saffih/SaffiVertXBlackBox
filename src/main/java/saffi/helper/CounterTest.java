package saffi.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class CounterTest {

    @Test
    public void testEmpty(){
        Counter counter = new Counter();
        counter.add(null);
    }


    @Test
    public void testGet(){
        Counter counter = new Counter();

        Assert.assertEquals(0, Math.toIntExact(counter.get("word")));
        counter.add("word");
        Assert.assertEquals(1, Math.toIntExact(counter.get("word")));

        counter.add("otherword");
        Assert.assertEquals(1, Math.toIntExact(counter.get("word")));

    }

    @Test
    public void testAsMap(){
        Counter counter = new Counter();

	    counter.add("word");
        Assert.assertEquals(
                Math.toIntExact(counter.asMap().get("word"))
		        , 1);

        for (Map.Entry<String, Integer> entry : counter.asCollection().entrySet()) {
            Assert.assertEquals("word", entry.getKey());
            Assert.assertEquals(new Integer(1), entry.getValue());
        }
    }

    @Test
    public void testAsJson(){
        Counter counter = new Counter();
        counter.add("word");
        Assert.assertEquals("{\"word\":1}", counter.asJson());
    }
}
