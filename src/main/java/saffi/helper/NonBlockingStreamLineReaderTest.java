package saffi.helper;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class NonBlockingStreamLineReaderTest {

	public static ByteArrayInputStream getByteArrayInputStream(String example) {
		return new ByteArrayInputStream(example.getBytes(StandardCharsets.UTF_8));
	}

	@Test
	public void testEmpty() throws IOException {
		InputStream stream = getByteArrayInputStream("");
		NonBlockingStreamLineReader sth = new NonBlockingStreamLineReader(stream);
		sth.getLine();
	}

	@Test
	public void testGoodMessage() throws IOException {
		String example = "{{{{{ \"event_type\": \"baz\", \"data\": \"ipsum\", \"timestamp\": 1474449973 }\n" +
				"{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n" +
				"{ \"%�\\~��\n" +
				"{ \"event_type\": \"bar\", \"data\": \"dolor\", \"timestamp\": 1474449973 }\n" +
				"H�+��\n" +
				"{ \"event_type\": \"bar\", \"data\": \"dolor\", \"timestamp\": 1474449980 }\n" +
				"{ \"event_type\": \"foo\", \"data\": \"ipsum\", \"timestamp\": 1474449980 }\n" +
				"{ \"s    ��\n" +
				"{ \"event_type\": \"bar\", \"data\": \"ipsum\", \"timestamp\": 1474449991 }\n" +
				"{ \"event_type\": \"baz\", \"data\": \"ipsum\", \"timestamp\": 1474449994 }\n" +
				"last\n"+" ignores";
		InputStream stream = getByteArrayInputStream(example);
		NonBlockingStreamLineReader sth = new NonBlockingStreamLineReader(stream);
		int cnt=0;
		String st;
		String last="";
		for (; (st = sth.getLine()) != null; ) {
			last=st;
			cnt++;
		}
		Assert.assertEquals(cnt, 11);
		Assert.assertEquals("last\n", last);

	}

}
