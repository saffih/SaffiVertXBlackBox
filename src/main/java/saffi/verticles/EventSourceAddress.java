package saffi.verticles;


public class EventSourceAddress {
    public static String getEventAll() {
        return getAddressPrefix() + ".eventcount.all";
    }

    public static String getWordAll() {
        return getAddressPrefix() + ".wordcount.all";
    }

    public static String getEventQuery() {
        return getAddressPrefix() + ".eventcount.query";
    }

    public static String getWordQuery() {
        return getAddressPrefix() + ".wordcount.query";
    }

    private static String getAddressPrefix() {
        return "saffi.dataevent";
    }
}
