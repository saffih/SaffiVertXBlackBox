package saffi.dataevent;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by saffi on 25/09/16.
 */
public class DataStreamHelperTest {
	public static ByteArrayInputStream getByteArrayInputStream(String example) {
		return new ByteArrayInputStream(example.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testHelper() throws IOException {
		String resNull = new DataStreamHelper(getByteArrayInputStream("Hello")).getString();
		Assert.assertNull(resNull);
		String st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";

		String res = new DataStreamHelper(getByteArrayInputStream(st)).getString();
		Assert.assertNotNull(res);
	}
}
