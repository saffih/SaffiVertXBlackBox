package saffi.dataevent;

import saffi.helper.WordCounter;


public class DataEventCounter {

	// rate is slow enough - we do not need long.
	private WordCounter wordCount = new WordCounter();
	private WordCounter eventCount = new WordCounter();

	public WordCounter getWordCount() {
		return wordCount;
	}

	public WordCounter getEventCount() {
		return eventCount;
	}

	public void add(DataEvent de) {
		if (de == null) {
			return;
		}
		eventCount.add(de.event_type);
		wordCount.add(de.data);
	}
}
