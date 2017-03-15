package saffi.verticles;


import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import saffi.dataevent.DataEvent;
import saffi.dataevent.DataEventHelper;
import saffi.helper.VertXDeploymentOptionsFactory;

import java.io.IOException;

@RunWith(VertxUnitRunner.class)
public class EventSourceTest {
    Vertx vertx;

    @Before
    public void setUp(TestContext context) throws IOException {

        vertx = Vertx.vertx();

        DeploymentOptions options = VertXDeploymentOptionsFactory.getTestOptions();
        options.getConfig().put(EventSource.disablePropertyName(), true);

        vertx.deployVerticle("saffi.verticles.EventSource", options, context.asyncAssertSuccess());
    }

    @After
    public void after(TestContext context) {

        vertx.close(context.asyncAssertSuccess());
    }


    @Test(timeout = 1000)
    public void testIgnoreBadEventsEvent(TestContext context) {

        Async async = context.async();
        String st = " \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
        final EventBus eb = vertx.eventBus();

        eb.send(JSONPumpAddress.getBroadcast(), st);
        eb.send(EventSourceAddress.getWordQuery(), "baz",
                reply -> {
                    context.assertEquals(0, reply.result().body());
                    async.complete();
                });
    }

    @Test(timeout = 3000L)
    public void testGoodEventsEvent(TestContext context) {
        Async async = context.async();
        String st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
        final EventBus eb = vertx.eventBus();

        Future<Void> fut1 = Future.future();
        Future<Void> fut2 = Future.future();

        fut1.setHandler(v -> {
            eb.send(EventSourceAddress.getEventQuery(), "baz",
                    reply -> {
                        context.assertEquals(0, reply.result().body());
                        eb.publish(JSONPumpAddress.getBroadcast(), st);
                        fut2.complete();
                    });
        });

        fut2.setHandler(v -> {
            eb.send(EventSourceAddress.getEventQuery(), "baz",
                    reply -> {
                        context.assertEquals(1, reply.result().body());
                        async.complete();
                    });
        });


        fut1.complete();
    }

    @Test(timeout = 1000)
    public void testIgnoreBadWordEvent(TestContext context) {
        Async async = context.async();
        String st = " \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
        final EventBus eb = vertx.eventBus();

        eb.send(JSONPumpAddress.getBroadcast(), st);
        eb.send(EventSourceAddress.getWordQuery(), "baz",
                reply -> {
                    context.assertEquals(0, reply.result().body());
                    async.complete();
                });
    }

    @Test(timeout = 3000L)
    public void testGoodWordEvent(TestContext context) {
        Async async = context.async();
        String st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
        final EventBus eb = vertx.eventBus();

        Future<Void> fut1 = Future.future();
        Future<Void> fut2 = Future.future();

        String s = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
        DataEvent de = DataEventHelper.fromJsonSilentFail(s);

        fut1.setHandler(v -> {
            eb.send(EventSourceAddress.getEventQuery(), "baz",
                    reply -> {
                        context.assertEquals(0, reply.result().body());
                        eb.publish(JSONPumpAddress.getBroadcast(), st);
                        fut2.complete();
                    });
        });

        fut2.setHandler(v -> {
            eb.send(EventSourceAddress.getEventQuery(), "baz",
                    reply -> {
                        context.assertEquals(1, reply.result().body());
                        async.complete();
                    });
        });


        fut1.complete();
    }


    @Test(timeout = 1000L)
    public void testAllEvents(TestContext context) {
        Async async = context.async();
        String st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
        final EventBus eb = vertx.eventBus();

        Future<Void> fut1 = Future.future();
        Future<Void> fut2 = Future.future();

        fut1.setHandler(v -> {
            eb.send(EventSourceAddress.getEventAll(), "",
                    reply -> {
                        context.assertEquals("{}", reply.result().body());
                        eb.publish(JSONPumpAddress.getBroadcast(), st);
                        fut2.complete();
                    });
        });

        fut2.setHandler(v -> {
            eb.send(EventSourceAddress.getEventAll(), "",
                    reply -> {
                        context.assertEquals("{\"baz\":1}", reply.result().body());
                        async.complete();
                    });
        });

        fut1.complete();
    }


    @Test(timeout = 1000L)
    public void testAllWords(TestContext context) {
        Async async = context.async();
        String st = "{ \"event_type\": \"baz\", \"data\": \"dolor\", \"timestamp\": 1474449973 }";
        final EventBus eb = vertx.eventBus();

        Future<Void> fut1 = Future.future();
        Future<Void> fut2 = Future.future();

        fut1.setHandler(v -> {
            eb.send(EventSourceAddress.getWordAll(), "",
                    reply -> {
                        context.assertEquals("{}", reply.result().body());
                        eb.publish(JSONPumpAddress.getBroadcast(), st);
                        fut2.complete();
                    });
        });

        fut2.setHandler(v -> {
            eb.send(EventSourceAddress.getWordAll(), "",
                    reply -> {
                        context.assertEquals("{\"dolor\":1}", reply.result().body());
                        async.complete();
                    });
        });

        fut1.complete();
    }


}
