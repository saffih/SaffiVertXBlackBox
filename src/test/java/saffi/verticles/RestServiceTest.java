package saffi.verticles;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
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

import java.io.IOException;
import java.util.HashMap;

@RunWith(VertxUnitRunner.class)
public class RestServiceTest {
    Vertx vertx;
    private Integer PORT = RestService.PORT_DEFAULT;
    private String testhost = "localhost";

    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();
        final DeploymentOptions options = ConfHelper.getDeploymentOptionsForTest();
        PORT = options.getConfig().getInteger("http.port", PORT);
        testhost = options.getConfig().getString("test.host", testhost);
        vertx.deployVerticle("saffi.verticles.RestService", options, context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) throws InterruptedException {
        vertx.close(context.asyncAssertSuccess());
        Thread.sleep(500);
    }


    @Test(timeout = 10000)
    public void testQueryWord(TestContext context) {

        Async async1 = context.async();
        HttpClient client = vertx.createHttpClient();
        final String id = "ping";
        final int cnt = 4;

        EventBus eb = vertx.eventBus();
        eb.consumer(EventSourceAddress.getWordQuery(), message ->
        {
            context.assertEquals(id, message.body());
            message.reply(cnt);
        });

        HttpClientRequest req = client.get(PORT, testhost, "/word/" + id);

        req.exceptionHandler(err -> context.fail(err.getMessage()));
        req.handler(resp -> {
            context.assertEquals(200, resp.statusCode());
            resp.bodyHandler(body -> {
                String resJson = new String(body.getBytes());
                HashMap res = Json.decodeValue(resJson, HashMap.class);
                context.assertEquals(cnt, res.get(id));
                async1.complete();

            });
        });
        req.end();
    }

    @Test(timeout = 1000)
    public void testQueryEvent(TestContext context) {

        Async async1 = context.async();
        HttpClient client = vertx.createHttpClient();
        final String id = "pong";
        final int cnt = 5;
        HttpClientRequest req = client.get(PORT, testhost, "/event/" + id);

        EventBus eb = vertx.eventBus();
        eb.consumer(EventSourceAddress.getEventQuery(), message ->
        {
            context.assertEquals(id, message.body());
            message.reply(cnt);
        });

        req.exceptionHandler(err -> context.fail(err.getMessage()));
        req.handler(resp -> {
            context.assertEquals(200, resp.statusCode());
            async1.complete();
        });
        req.end();
    }

    @Test(timeout = 1000)
    public void testAllWords(TestContext context) {

        Async async1 = context.async();
        HttpClient client = vertx.createHttpClient();
        final String data = "data";
        HttpClientRequest req = client.get(PORT, testhost, "/words");

        EventBus eb = vertx.eventBus();
        eb.consumer(EventSourceAddress.getWordAll(), message ->
        {
            message.reply(data);
        });

        req.exceptionHandler(err -> context.fail(err.getMessage()));
        req.handler(resp -> {
            context.assertEquals(200, resp.statusCode());
            async1.complete();
        });
        req.end();
    }

    @Test(timeout = 1000)
    public void testAllEvents(TestContext context) {

        Async async1 = context.async();
        HttpClient client = vertx.createHttpClient();
        final String data = "data";
        HttpClientRequest req = client.get(PORT, testhost, "/events");

        EventBus eb = vertx.eventBus();
        eb.consumer(EventSourceAddress.getEventAll(), message ->
        {
            message.reply(data);
        });

        req.exceptionHandler(err -> context.fail(err.getMessage()));
        req.handler(resp -> {
            context.assertEquals(200, resp.statusCode());
            async1.complete();
        });
        req.end();
    }

}
