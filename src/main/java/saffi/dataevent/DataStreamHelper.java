package saffi.dataevent;

import saffi.helper.NonBlockingStreamLineReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class DataStreamHelper extends NonBlockingStreamLineReader {
    public DataStreamHelper(InputStream stream) {
        super(stream);
    }

    @Override
    public String getLine() throws IOException {
        while (true) {
            String st = super.getLine();
            if (st == null) {
                return null;
            }
            st = DataEventHelper.fromJsonAndBackSilentFail(st);
            if (st != null) {
                return st;
            }
        }
    }

    public static DataStreamHelper getDataStreamHelper(String st) {
        return new DataStreamHelper(getByteArrayInputStream(st));
    }

    public static ByteArrayInputStream getByteArrayInputStream(String example) {
        return new ByteArrayInputStream(example.getBytes(StandardCharsets.UTF_8));
    }
}
