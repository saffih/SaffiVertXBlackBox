package saffi.dataevent;

import saffi.helper.NonBlockingStreamLineReader;

import java.io.IOException;
import java.io.InputStream;

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
}
