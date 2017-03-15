package saffi.dataevent;

import lombok.Data;

import java.io.Serializable;


public
@Data
class DataEvent implements Serializable {

    String event_type;
    String data;
    long timestamp;

    DataEvent(String event_type, String data, long timestamp) {
        this.event_type = event_type;
        this.data = data;
        this.timestamp = timestamp;
    }
}
