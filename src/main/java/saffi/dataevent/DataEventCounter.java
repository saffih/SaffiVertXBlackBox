package saffi.dataevent;

import saffi.helper.Counter;


public class DataEventCounter {

    // rate is slow enough - we do not need long.
    private Counter wordCount=new Counter();
    private Counter eventCount=new Counter();

    public Counter getWordCount() {
        return wordCount;
    }

    public Counter getEventCount() {
        return eventCount;
    }

    public void add(DataEvent de){
        if (de==null){
            return;
        }
        eventCount.add(de.event_type);
        wordCount.add(de.data);
    }
}
