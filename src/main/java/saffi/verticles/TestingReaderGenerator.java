package saffi.verticles;

import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import saffi.helper.ArrayStreamLineReader;

import java.io.IOException;


public class TestingReaderGenerator extends ReaderGenerator {
    private final EventBus eb;
    private MessageConsumer<Object> fakeConsumer = null;
    private ArrayStreamLineReader streamLineReader;

    public TestingReaderGenerator(EventBus eb) {
        this.eb = eb;
    }

    public String getLine() throws IOException {
        return streamLineReader.getLine();
    }

    void start(Future<Void> started) {
        streamLineReader = createFakeStreamLineReader();
        started.complete();
    }

    public void stop(Future<Void> stopped) {
        fakeConsumer.unregister();
        stopped.complete();
    }

    private ArrayStreamLineReader createFakeStreamLineReader() {
        final ArrayStreamLineReader fakeHelper = new ArrayStreamLineReader();
        fakeConsumer = eb.consumer(JSONPump.fakePrefix(), msg -> {
            fakeHelper.add((String) msg.body());
        });
        return fakeHelper;
    }

}
