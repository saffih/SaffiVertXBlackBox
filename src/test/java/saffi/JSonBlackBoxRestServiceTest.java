package saffi;


import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.HashMap;

@RunWith(VertxUnitRunner.class)
public class JSonBlackBoxRestServiceTest {

	Vertx vertx;
	private Integer port;

	@Before
	public void setUp(TestContext context) throws IOException {
		Future<Object> started = Future.future().setHandler(context.asyncAssertSuccess());
		DeploymentOptions options = new DeploymentOptions()
				.setConfig(new JsonObject().put("http.port", 8080)
				);
		port = options.getConfig().getInteger("http.port");
		vertx = JSonBlackBoxRestService.serviceStart(options, started);
	}

	@After
	public void after(TestContext context) {

		vertx.close(context.asyncAssertSuccess());
	}


	@Test(timeout = 1000)
	public void testIntegration(TestContext context) {

		Async async1 = context.async();
		HttpClient client = vertx.createHttpClient();
		final String id = "baz";
		HttpClientRequest req = client.get(port, "localhost", "/events");

		req.exceptionHandler(err -> context.fail(err.getMessage()));
		req.handler(resp -> {
			context.assertEquals(200, resp.statusCode());
			async1.complete();
		});
		req.end();
	}

	@Test(timeout = 3000)
	public void blackBoxTypeBaz(TestContext context) {
		Async async = context.async();

		Future<String> foundBaz = Future.future();

		pollForEvent("baz", context, foundBaz);

		Future<String> foundBazDone = Future.future();

		foundBaz.setHandler(res -> {
			System.out.println("found baz" + res.result());
			foundBazDone.complete();
			async.complete();
		});
	}

	@Test(timeout = 15000)
	public void blackBoxTypeBazAndBar(TestContext context) {
		Async async = context.async();

		Future<String> foundBaz = Future.future();
		Future<String> foundBar = Future.future();

		pollForEvent("baz", context, foundBaz);
		pollForEvent("bar", context, foundBar);



		Future<String> foundBazDone = Future.future();
		Future<String> foundBarDone= Future.future();

		foundBar.setHandler(res -> {
			System.out.println("found bar" + res.result());
			foundBarDone.complete();
		});

		foundBaz.setHandler(res -> {
			System.out.println("found baz" + res.result());
			foundBazDone.complete();
		});

		CompositeFuture.join(foundBarDone, foundBazDone).setHandler(res -> {
			async.complete();
		});

	}

	public void pollForEvent(String id, TestContext context, Future<String> success) {
		long timerid[] = {0};
		timerid[0] = vertx.setPeriodic(100, v -> {
			queryFor(context, success, id);
			if (success.isComplete()) {
				vertx.cancelTimer(timerid[0]);
			}
		});
	}

	public void queryForBaz(TestContext context, Future<String> success) {

		final String id = "baz";
		queryFor(context, success, id);
	}

	public void queryForBar(TestContext context, Future<String> success) {

		final String id = "bar";
		queryFor(context, success, id);
	}

	public void queryFor(TestContext context, Future<String> success, String id) {
		HttpClient client = vertx.createHttpClient();
		HttpClientRequest req = client.get(port, "localhost", "/event/" + id);

		req.exceptionHandler(err -> context.fail(err.getMessage()));
		req.handler(resp -> {
			context.assertEquals(200, resp.statusCode());
			resp.bodyHandler(body -> {
				String resJson = new String(body.getBytes());
				HashMap res = Json.decodeValue(resJson, HashMap.class);
				final Integer value = (Integer) res.getOrDefault(id, null);
				if (new Integer(1).equals(value)) {
					success.complete("Success");
				} else {
					System.out.print(".");
				}
			});
		});
		req.end();
	}

}
