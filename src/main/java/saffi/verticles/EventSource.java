package saffi.verticles;

import io.vertx.core.*;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import saffi.dataevent.DataEvent;
import saffi.dataevent.DataEventCounter;
import saffi.dataevent.DataEventHelper;

import static saffi.verticles.JSONPumpAddress.getBroadcast;


public class EventSource extends AbstractVerticle {

	private DataEventCounter counter = new DataEventCounter();
	private MessageConsumer<Object> brodcastConsumer;
	private MessageConsumer<Object> wordQueryConsumer;
	private MessageConsumer<Object> eventQueryConsumer;
	private MessageConsumer<Object> wordAllConsumer;
	private MessageConsumer<Object> eventAllConsumer;

	public void start(Future<Void> started) {
		Future<Void> attachBus = Future.future();
		Future<Void> spawnChild = Future.future();

		attachBus.setHandler(v->{
			setupChildProcess(spawnChild);
		});

		spawnChild.setHandler(v-> {
			started.complete();
		});

		setupMessageConsumers(attachBus);

	}

	public static String disablePropertyName(){
		return "disable." + EventSource.class.getSimpleName();
	}

	private void setupChildProcess(Future<Void> spawnChild) {
		Boolean useFakeStream = config().getBoolean(disablePropertyName(), false);
		if (useFakeStream) {
			spawnChild.complete();
			return;
		}
		vertx.deployVerticle(new JSONPump(), ar->spawnChild.complete());
	}

	private void setupMessageConsumers(Future<Void> fut) {
		EventBus eb = vertx.eventBus();
		brodcastConsumer = eb.consumer(getBroadcast(), message -> {
			final String body = (String) message.body();
			DataEvent de = DataEventHelper.fromJsonSilentFail(body);
			counter.add(de);
		});

		wordQueryConsumer = eb.consumer(EventSourceAddress.getWordQuery(), message -> {
			String word = (String) message.body();
			Integer cnt = counter.getWordCount().get(word);
			message.reply(cnt);
		});


		eventQueryConsumer = eb.consumer(EventSourceAddress.getEventQuery(), message -> {
			String word = (String) message.body();
			Integer cnt = counter.getEventCount().get(word);
			message.reply(cnt);
		});

		wordAllConsumer = eb.consumer(EventSourceAddress.getWordAll(), message -> {
			message.reply(counter.getWordCount().asJson());
		});

		eventAllConsumer = eb.consumer(EventSourceAddress.getEventAll(), message -> {
			message.reply(counter.getEventCount().asJson());
		});

		// Done setup consumers ready on event bus.
		fut.complete();
	}

	// Optional - called when verticle is undeployed
	public void stop() {
		brodcastConsumer.unregister();
		wordQueryConsumer.unregister();
		eventQueryConsumer.unregister();
		wordAllConsumer.unregister();
		eventAllConsumer.unregister();
	}


//	public static void main(String[] args) {
//		Vertx vertx = Vertx.vertx();
//		vertx.deployVerticle(new EventSource());
//		vertx.deployVerticle(new RestService());
//		vertx.eventBus().consumer(getBroadcast(), message -> {
//			System.out.println("I have received a message: " + message.body());
//		});
//	}
}
