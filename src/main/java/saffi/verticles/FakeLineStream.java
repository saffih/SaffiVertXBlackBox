package saffi.verticles;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import saffi.helper.ArrayLineReader;

import java.io.IOException;


public class FakeLineStream extends LineStream {
	private final EventBus eb;
	private MessageConsumer<Object> fakeConsumer = null;
	private ArrayLineReader streamLineReader;

	public String getLine() throws IOException {
		return streamLineReader.getLine();
	}

	public FakeLineStream(EventBus eb) {
		this.eb=eb;
	}
	void start(Future<Void> started) {
		streamLineReader = createFakeStreamLineReader();
		started.complete();
	}

	public void stop(Future<Void> stopped) {
		fakeConsumer.unregister();
		stopped.complete();
	}

	private ArrayLineReader createFakeStreamLineReader() {
		final ArrayLineReader fakeHelper = new ArrayLineReader();
		fakeConsumer = eb.consumer(JSONPump.fakePrefix(), msg -> {
			fakeHelper.add((String) msg.body());
		});
		return fakeHelper;
	}

}
