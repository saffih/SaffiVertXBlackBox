package saffi.dataevent;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class DataEventHelperTest {

	@Test
	public void testDataEventParse() {
		DataEvent de = new DataEvent("type", "data", 1334L);
		String st = DataEventHelper.toJson(de);
		DataEvent de2 = DataEventHelper.fromJsonSilentFail(st);
		Assert.assertEquals(de, de2);
		HashMap<DataEvent, DataEvent> map = new HashMap<>();
		map.put(de, de);
		Assert.assertTrue(map.get(de2) == de);
	}

	@Test
	public void tesFailSafe() {
		DataEvent de2 = DataEventHelper.fromJsonSilentFail("{{1,2,3}");
		Assert.assertNull(de2);
		String st = DataEventHelper.fromJsonAndBackSilentFail("{{1,2,3}");
		Assert.assertNull(st);
	}
}
