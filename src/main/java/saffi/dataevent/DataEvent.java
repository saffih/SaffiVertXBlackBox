package saffi.dataevent;

import java.io.Serializable;

public class DataEvent implements Serializable {

	String event_type;
	String data;
	long timestamp;

	public DataEvent(String event_type, String data, long timestamp) {
		this.event_type = event_type;
		this.data = data;
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DataEvent dataEvent = (DataEvent) o;

		if (timestamp != dataEvent.timestamp) return false;
		if (event_type != null ? !event_type.equals(dataEvent.event_type) : dataEvent.event_type != null) return false;
		return data != null ? data.equals(dataEvent.data) : dataEvent.data == null;

	}

	@Override
	public int hashCode() {
		int result = event_type != null ? event_type.hashCode() : 0;
		result = 31 * result + (data != null ? data.hashCode() : 0);
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

}
