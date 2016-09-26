package saffi.helper;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * For single threaded use.
 * Iterator of the collection.
 */
public class WordCounter implements Cloneable {

    // rate is slow enough - we do not need long.
    private ConcurrentHashMap<String, Integer> counter = new ConcurrentHashMap<String, Integer>();

    public void add(String st) {
        if (st == null) {
            return;
        }
        counter.put(st, counter.getOrDefault(st, 0) + 1);
    }


    public Integer get(String st) {
        return counter.getOrDefault(st, 0);
    }


    public Map<String, Integer> asMap() {
        return Collections.unmodifiableMap(counter);
    }


    public String asJson() {
        Gson g = new Gson();
        return g.toJson(counter);
    }

}
