package saffi.verticles;

import io.vertx.core.Future;
import saffi.dataevent.DataStreamHelper;

import java.io.IOException;
import java.io.InputStream;


public class BlackBoxReaderGenerator extends ReaderGenerator {
    private String command = "./generator-linux-amd64";
    private DataStreamHelper streamLineReader;

    public BlackBoxReaderGenerator(String command) {
        this.command = command;
    }

    public String getLine() throws IOException {
        return streamLineReader.getLine();
    }

    void start(Future<Void> started) {
        InputStream stream = getBlackBoxInputStream();
        streamLineReader = new DataStreamHelper(stream);
        started.complete();
    }

    private InputStream getBlackBoxInputStream() {
        try {
            Process process = Runtime.getRuntime().exec(command);
            if (!process.isAlive()) {
                throw new RuntimeException("process execute: " + command);
            }
            return process.getInputStream();
        } catch (IOException e) {
            // todo notify, respawn - it should be done by the parent
            final String message = "spawn failed:" + command;
            throw new RuntimeException(message, e);
        }
    }


    public void stop(Future<Void> stopped) {
        // todo - Kill the process  ?
        stopped.complete();
    }

}
