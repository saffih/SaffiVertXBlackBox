package saffi.verticles;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import saffi.helper.ConfHelper;

import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public class JSONPumpTest {
	Vertx vertx;
	HttpServer server;

	@Before
	public void setUp(TestContext context) throws IOException {
		vertx = Vertx.vertx();
		DeploymentOptions options = ConfHelper.getDeploymentOptionsForTest();
		options.getConfig().put(JSONPump.fakePrefix(), true);
		vertx.deployVerticle("saffi.verticles.JSONPump", options, context.asyncAssertSuccess());
	}

	@After
	public void after(TestContext context) {

		vertx.close(context.asyncAssertSuccess());
	}


	@Test(timeout = 3000)
	public void testWithFakeGoodEvent(TestContext context) {
		Async async = context.async();
		String st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
		final String[] message = new String[1];
		final EventBus eb = vertx.eventBus();
		eb.consumer(JSONPumpAddress.getBroadcast(), msg -> {
			message[0] = (String) msg.body();
			context.assertEquals(st, message[0]);
			async.complete();
		});
		eb.send(JSONPump.fakePrefix(), st);
	}

}
