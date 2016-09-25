package saffi.dataevent;

import saffi.helper.StreamHelper;

import java.io.IOException;
import java.io.InputStream;

public class DataStreamHelper extends StreamHelper {
    public DataStreamHelper(InputStream stream) {
        super(stream);
    }

    @Override
    public String getString() throws IOException {
        while(true){
            String st = super.getString();
            if (st==null){
                return null;
            }
            st = DataEventHelper.fromJsonAndBackSilentFail(st);
            if (st!=null){
                return st;
            }
        }
    }
}
