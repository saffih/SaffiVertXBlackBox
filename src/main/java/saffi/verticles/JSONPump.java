package saffi.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import saffi.helper.StreamLineReader;

import java.io.IOException;



public class JSONPump extends AbstractVerticle {
	Logger logger = LoggerFactory.getLogger(JSONPump.class);

	private int pollInterval = 100;
	private String command = "./generator-linux-amd64";

	private ReaderGenerator readerGenerator = null;
	private long timerId;

	public static String fakePrefix() {
		return "fake." + JSONPump.class.getClass().getSimpleName();
	}


	public void start(Future<Void> started) {
		pollInterval = config().getInteger("pollInterval", pollInterval);

		Boolean useFake = config().getBoolean(fakePrefix(), false);
		if (useFake) {
			readerGenerator = new TestingReaderGenerator(vertx.eventBus());
		}
		else{
			command = config().getString("blackbox", command);

			// since we read in a non blocking way - we do not need to put it within a worker
			// vertx.executeBlocking(future -> {...}, res -> {});
			readerGenerator = new BlackBoxReaderGenerator(command);
		}
		pollStreamSendEvents(readerGenerator);
		readerGenerator.start(started);
	}


	// Optional - called when verticle is undeployed
	public void stop(Future<Void> stopped) {
		vertx.cancelTimer(timerId);
		readerGenerator.stop(stopped);
	}

	private void pollStreamSendEvents(StreamLineReader streamLineReader) {
		timerId = vertx.setPeriodic(pollInterval, id -> {
			while (consumeAvailable(streamLineReader) != null) {
			// do that again
			}
		});
	}

	private String consumeAvailable(StreamLineReader helper) {
		try {
			String st = helper.getLine();
			if (st == null) {
				return null;
			}
			EventBus eb = vertx.eventBus();
			eb.publish(JSONPumpAddress.getBroadcast(), st);
			return st;
		} catch (IOException e) {
			// failures should crash and a new one should be created
			throw new RuntimeException("failed readiing stream ", e);
		}
	}

}
