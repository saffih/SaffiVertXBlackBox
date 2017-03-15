package saffi.helper;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class WordCounterTest {

    @Test
    public void testEmpty() {
        WordCounter wordCounter = new WordCounter();
        wordCounter.add(null);
    }


    @Test
    public void testGet() {
        WordCounter wordCounter = new WordCounter();

        Assert.assertEquals(0, Math.toIntExact(wordCounter.get("word")));
        wordCounter.add("word");
        Assert.assertEquals(1, Math.toIntExact(wordCounter.get("word")));

        wordCounter.add("otherword");
        Assert.assertEquals(1, Math.toIntExact(wordCounter.get("word")));

    }

    @Test
    public void testAsMap() {
        WordCounter wordCounter = new WordCounter();

        wordCounter.add("word");
        Assert.assertEquals(1, wordCounter.asMap().get("word").intValue());

        for (Map.Entry<String, Integer> entry : wordCounter.asMap().entrySet()) {
            Assert.assertEquals("word", entry.getKey());
            Assert.assertEquals(1, entry.getValue().intValue());
        }
    }

    @Test
    public void testAsJson() {
        WordCounter wordCounter = new WordCounter();
        wordCounter.add("word");
        Assert.assertEquals("{\"word\":1}", wordCounter.asJson());
    }
}
