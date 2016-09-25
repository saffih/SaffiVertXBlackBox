package saffi;

import io.vertx.core.*;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import saffi.verticles.JSONPumpAddress;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JSonBlackBoxRestService  extends AbstractVerticle {

	private static String eventSourceId;
	private static String restServiceId;
	private MessageConsumer<Object> consoleOut;

	@Override
	public void start(Future<Void> started) {
		Future<Void> fut1 = Future.future();
		vertx.deployVerticle("saffi.verticles.EventSource", res -> {
			if (res.succeeded()) {
				eventSourceId = res.result();
				System.out.println("Deployment id is: " + res.result());
				fut1.complete();
			} else {
				final String msg = "Deployment failed!";
				fut1.fail(msg);
				System.out.println(msg);
			}
		});


		fut1.setHandler(v -> {
			DeploymentOptions options = new DeploymentOptions();
			options.setConfig(config());
			vertx.deployVerticle( "saffi.verticles.RestService", options, res -> {
				if (res.succeeded()) {
					restServiceId = res.result();
					System.out.println("Deployment id is: " + res.result());
				} else {
					System.out.println("Deployment failed!");
					started.failed();
				}
				started.complete();
			});
		});

		consoleOut= vertx.eventBus().consumer(JSONPumpAddress.getBroadcast(), message -> {
			System.out.println("I have received a message: " + message.body());
		});
	}



	public void stop(Future<Void> stopped) {
		Future<Void> fut1= Future.future();
		Future<Void> fut2= Future.future();
		Future<Void> fut3= Future.future();
		consoleOut.unregister(fut3.completer());
		System.out.println("Undeploy id is: " + restServiceId);
		vertx.undeploy(restServiceId, fut1.completer());
		System.out.println("Undeploy id is: " + eventSourceId);
		vertx.undeploy(eventSourceId, fut2.completer());

		CompositeFuture.join(fut1,fut2, fut3).setHandler(v->{
			System.out.println("Undeployed done");
			stopped.complete();
		});
	}

	public static  DeploymentOptions getDeploymentOptions() {
		String runDir = System.getProperty("user.dir");
		System.out.println("running dir :"+runDir);
		final String path = "src/main/conf/json-blackbox.json";
		String jsonConf = readFile(path, StandardCharsets.UTF_8);

		final DeploymentOptions options = new DeploymentOptions();
		options.setConfig(new JsonObject(jsonConf));
		return options;
	}


	private static String readFile(String path, Charset encoding)
	{
		byte[] encoded = new byte[0];
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			throw new RuntimeException("can't find config at  "+path,e);
		}
		return new String(encoded, encoding);
	}




//	public static void main(String[] args) throws InterruptedException {
//		Future<Void> started = Future.future();
//
//	}
}
