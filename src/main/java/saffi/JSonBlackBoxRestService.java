package saffi;

import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import saffi.helper.ConfHelper;
import saffi.verticles.JSONPump;
import saffi.verticles.JSONPumpAddress;

import static saffi.helper.ConfHelper.getDeploymentOptions;

public class JSonBlackBoxRestService extends AbstractVerticle {
	private static String eventSourceId;
	private static String restServiceId;
	Logger logger = LoggerFactory.getLogger(JSONPump.class);
	private MessageConsumer<Object> consoleOut;

	public static void main(String[] args) {

		Vertx vertx = Vertx.vertx();
		final DeploymentOptions options = ConfHelper.getDeploymentOptionsForTest();
		vertx.deployVerticle("saffi.JSonBlackBoxRestService", options);
	}

	@Override
	public void start(Future<Void> started) {

		Future<Void> fut1 = Future.future();
		vertx.deployVerticle("saffi.verticles.EventSource",
				getDeploymentOptions(this), res -> {
					if (res.succeeded()) {
						eventSourceId = res.result();
						System.out.println("Deployment id is: " + res.result());
						fut1.complete();
					} else {
						final String msg = "Deployment failed!";
						System.out.println(msg);
						fut1.fail(msg);
					}
				});


		fut1.setHandler(v -> {
			DeploymentOptions options = new DeploymentOptions();
			options.setConfig(config());
			vertx.deployVerticle("saffi.verticles.RestService", options, res -> {
				if (res.succeeded()) {
					restServiceId = res.result();
					System.out.println("Deployment id is: " + res.result());
					started.complete();
				} else {
					final String msg = "Deployment failed!";
					System.out.println(msg);
					started.fail(msg);
				}
			});
		});

		consoleOut = vertx.eventBus().consumer(JSONPumpAddress.getBroadcast(), message -> {
			System.out.println("I have received a message: " + message.body());
		});
	}

	public void stop(Future<Void> stopped) {
		Future<Void> fut1 = Future.future();
		Future<Void> fut2 = Future.future();
		Future<Void> fut3 = Future.future();

		consoleOut.unregister(fut3.completer());

		System.out.println("Undeploy id is: " + restServiceId);
		vertx.undeploy(restServiceId, fut1.completer());

		System.out.println("Undeploy id is: " + eventSourceId);
		vertx.undeploy(eventSourceId, fut2.completer());

		CompositeFuture.join(fut1, fut2, fut3).setHandler(v -> {
			System.out.println("Undeployed done");
			stopped.complete();
		});
	}
}
