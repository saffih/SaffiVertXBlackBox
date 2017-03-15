package saffi.helper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by saffi on 15/03/17.
 */
public class ArrayStreamLineReaderTest {
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void empty() throws Exception {
        ArrayStreamLineReader a = new ArrayStreamLineReader();
        assert a.getLine() == null;
    }

    @Test
    public void getBack() throws Exception {
        ArrayStreamLineReader a = new ArrayStreamLineReader();
        final String line = "line";
        a.add(line);
        assert a.getLine().equals(line);
        assert a.getLine() == null;
    }

}