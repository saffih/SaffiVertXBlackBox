package saffi.dataevent;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class DataStreamHelperTest {

    @Test
    public void testHelper() throws IOException {
        final String hello = "Hello";
        String resNull = DataStreamHelper.getDataStreamHelper(hello).getLine();
        Assert.assertNull(resNull);
        String st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n";

        String res = DataStreamHelper.getDataStreamHelper(st).getLine();
        Assert.assertNotNull(res);
    }
}
