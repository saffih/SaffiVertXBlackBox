package saffi;


import io.vertx.core.CompositeFuture;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.json.Json;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import saffi.helper.ConfHelper;
import saffi.verticles.RestService;

import java.io.IOException;
import java.util.HashMap;

import static saffi.helper.ConfHelper.getDeploymentOptions;

@RunWith(VertxUnitRunner.class)
public class JSonBlackBoxRestServiceTest {

	Vertx vertx;
	private int port = RestService.PORT_DEFAULT;
	private String testhost = "localhost";

	@Before
	public void setUp(TestContext context) throws IOException {
		vertx = Vertx.vertx();
		final DeploymentOptions options = ConfHelper.getDeploymentOptionsForTest();
		port = options.getConfig().getInteger("http.port", port);
		testhost = options.getConfig().getString("test.host", testhost);
		vertx.deployVerticle("saffi.JSonBlackBoxRestService", options,
				context.asyncAssertSuccess());
	}

	@After
	public void after(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}


	@Test(timeout = 2000)
	public void testEvents(TestContext context) {

		Async async1 = context.async();
		HttpClient client = vertx.createHttpClient();
		HttpClientRequest req = client.get(port, testhost, "/events");

		req.exceptionHandler(err -> context.fail(err.getMessage()));
		req.handler(resp -> {
			context.assertEquals(200, resp.statusCode());

			resp.bodyHandler(body -> {
				String resJson = new String(body.getBytes());
				context.assertEquals("{}", resJson);
				async1.complete();
			});
		});
		req.end();
	}


	@Test(timeout = 2000)
	public void testWords(TestContext context) {

		Async async1 = context.async();
		HttpClient client = vertx.createHttpClient();
		HttpClientRequest req = client.get(port, testhost, "/words");

		req.exceptionHandler(err -> context.fail(err.getMessage()));
		req.handler(resp -> {
			context.assertEquals(200, resp.statusCode());

			resp.bodyHandler(body -> {
				String resJson = new String(body.getBytes());
				// real app the first event is fast
//				context.assertEquals("{}", resJson);
				async1.complete();
			});
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
			System.out.println("found baz " + res.result());
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
		Future<String> foundBarDone = Future.future();

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
		Future<String> found = Future.future();

		long timerid[] = {0};

		timerid[0] = vertx.setPeriodic(100, v -> {
			queryFor(context, found, id);
		});

		found.setHandler(ar -> {
			vertx.cancelTimer(timerid[0]);
			success.complete(found.result());
		});
	}

	public void queryFor(TestContext context, Future<String> success, String id) {
		HttpClient client = vertx.createHttpClient();
		HttpClientRequest req = client.get(port, testhost, "/event/" + id);

		req.exceptionHandler(err -> context.fail(err.getMessage()));
		req.handler(resp -> {
			context.assertEquals(200, resp.statusCode());

			resp.bodyHandler(body -> {
				String resJson = new String(body.getBytes());
				HashMap res = Json.decodeValue(resJson, HashMap.class);
				final Integer value = (Integer) res.getOrDefault(id, null);

				if (value!=0) {
					success.complete(resJson);
				} else {
					// waiting - next one might be a match.
					System.out.print(".");
				}
			});
		});
		req.end();
	}
}
