package saffi.dataevent;

import com.google.gson.Gson;

public class DataEventHelper {
    private final static Gson g = new Gson();

    private static DataEvent fromJson(String st) {
        return g.fromJson(st, DataEvent.class);
    }

    public static String toJson(DataEvent de) {
        return g.toJson(de);
    }

    public static DataEvent fromJsonSilentFail(String st) {
        try {
            return fromJson(st);
        } catch (RuntimeException e) {
            // logger
            return null;
        }
    }

    static String fromJsonAndBackSilentFail(String st) {
        try {
            return toJson(fromJson(st));
        } catch (RuntimeException e) {
            // logger
            return null;
        }
    }
}
