package saffi;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import org.junit.Test;
import saffi.verticles.EventSource;
import saffi.verticles.JSONPumpAddress;
import saffi.verticles.RestService;

public class JSonBlackBoxRestService {

	private static Vertx vertx;

	public static void main(String[] args) throws InterruptedException {
		Future<Object> started = Future.future();
		serviceStart(new DeploymentOptions(), started);

	}

	public static Vertx serviceStart(DeploymentOptions options, Future<Object> started) {

		vertx = Vertx.vertx();
		Future<Void> fut1 = Future.future();
		vertx.deployVerticle(new EventSource(), options, res -> {
			if (res.succeeded()) {
				System.out.println("Deployment id is: " + res.result());
				fut1.complete();
			} else {
				final String msg = "Deployment failed!";
				fut1.fail(msg);
				System.out.println(msg);
			}
		});

		fut1.setHandler(v -> {
			vertx.deployVerticle(new RestService(), options, res -> {
				if (res.succeeded()) {
					System.out.println("Deployment id is: " + res.result());
				} else {
					System.out.println("Deployment failed!");
				}
				started.complete();
			});
		});

		vertx.eventBus().consumer(JSONPumpAddress.getBroadcast(), message -> {
			System.out.println("I have received a message: " + message.body());
		});
		return vertx;
	}


}
