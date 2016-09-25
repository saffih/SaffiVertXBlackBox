package saffi.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;
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
        counter.add("word");
    }

    @Test
    public void testAsMap(){
        Counter counter = new Counter();
	    counter.add("word");
        Assert.assertEquals(Math.toIntExact(counter.asMap().get("word"))
		        , 1);
	    for( Iterator<Map.Entry<String, Integer>> it = counter.asCollection().entrySet().iterator();
	         it.hasNext();){
		    Map.Entry<String, Integer> entry = it.next();
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
