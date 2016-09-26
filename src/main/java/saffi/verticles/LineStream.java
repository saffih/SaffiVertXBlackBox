package saffi.verticles;

import io.vertx.core.Future;
import saffi.helper.StreamLineReader;


abstract class LineStream implements StreamLineReader {
	abstract void start(Future<Void> started);

	abstract void stop(Future<Void> stopped);

}
