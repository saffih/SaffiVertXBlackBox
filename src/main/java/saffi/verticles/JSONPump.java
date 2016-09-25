package saffi.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import saffi.dataevent.DataStreamHelper;
import saffi.helper.IStreamHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class JSONPump extends AbstractVerticle {

	private int pollInterval = 1000;
	private String command="./generator-linux-amd64";

	private MessageConsumer<Object> fakeConsumer=null;

	public static String fakePrefix(){
		return "fake."+JSONPump.class.getClass().getSimpleName()	;
	}


	public void start(Future<Void> fut) {
		command = config().getString("blackbox", command);
		pollInterval = config().getInteger("pollInterval", pollInterval);

		Boolean useFake = config().getBoolean(fakePrefix(), false);
		if (useFake) {
			spawnFakeBlackBox(fut);
			return;
		}
		// since we read in a non blocking way - we do not need to put it within a worker
		// vertx.executeBlocking(future -> {...}, res -> {});
		spawnNonBlockingBlackBox(fut);
	}



	// Optional - called when verticle is undeployed
	public void stop() {
		if (fakeConsumer!=null){
			fakeConsumer.unregister();
		}
	}

	private void spawnNonBlockingBlackBox(Future<Void> fut) {
		pollStreamSendEvents(createStreamHelperAndBlackBox(), vertx);
		fut.complete();

	}

	private IStreamHelper createStreamHelperAndBlackBox() {
		InputStream stream = getBlackBoxInputStream();
		return new DataStreamHelper(stream);
	}

	private InputStream getBlackBoxInputStream() {
		try {
		Process process = Runtime.getRuntime().exec(command);
			return  process.getInputStream();
		} catch (IOException e) {
			// todo notify, respawn - it should be done by the parent
			throw new RuntimeException("spawn failed", e);
		}
	}

	private void pollStreamSendEvents(IStreamHelper helper, Vertx vertx) {
		vertx.setPeriodic(pollInterval, id -> {
			consumeAvailable(helper);
		});
	}

	private void consumeAvailable(IStreamHelper helper) {
		try {
			String st = helper.getString();
			if (st == null) {
				return;
			}
			EventBus eb = vertx.eventBus();
			eb.publish(JSONPumpAddress.getBroadcast(), st);
		} catch (IOException e) {
			// failures should crash and a new one should be created
			throw new RuntimeException("failed readiing stream ", e);
		}
	}

	private void spawnFakeBlackBox(Future<Void> fut) {
		pollStreamSendEvents(createFakeHelper(), vertx);
		fut.complete();
	}


	private IStreamHelper  createFakeHelper() {
		final FakeStreamHelper fakeHelper= new FakeStreamHelper();
		final EventBus eb = vertx.eventBus();
		fakeConsumer = eb.consumer(fakePrefix(), msg -> {
			fakeHelper.buffer.add((String) msg.body());
		});
		return fakeHelper;
	}

	private class FakeStreamHelper implements IStreamHelper{
		ArrayList<String> buffer=new ArrayList<String>();
		@Override
		public String getString() throws IOException {
			if (buffer.isEmpty()){return null;}
			return buffer.remove(0);
		}
	}

}
