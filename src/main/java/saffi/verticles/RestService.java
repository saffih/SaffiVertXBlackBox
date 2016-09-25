package saffi.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


public class RestService extends AbstractVerticle {
	public static final int PORT_DEFAULT = 8080;
	private EventBus eb;
	private HttpServer server;
	private int port=PORT_DEFAULT;

	@Override
	public void start(Future<Void> started) {
		eb = vertx.eventBus();
		port = context.config().getInteger("http.port", port);

		Future<HttpServer> serverFuture = Future.future();
		serverFuture.setHandler(
				hs -> {
					started.complete();
				});

		Router router = setRoutes();

		server = vertx.createHttpServer().requestHandler(router::accept).listen(
				port,
				serverFuture.completer());
	}


	public void stop(Future<Void> stopped) {
		server.close(stopped.completer());
	}

	private Router setRoutes() {
		Router router = Router.router(vertx);

		router.get("/words").handler(allWordHandler());
		router.get("/events").handler(allTypeHandler());
		router.get("/word/:id").handler(queryWordHandler());
		router.get("/event/:id").handler(queryEventHandler());
		return router;
	}

	private Handler<RoutingContext> allTypeHandler() {
		return rc -> {
			eb.send(EventSourceAddress.getEventAll(), "", reply -> {
				Message msg = reply.result();
				String st = (String) msg.body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(st);
			});
		};
	}

	private Handler<RoutingContext> allWordHandler() {
		return rc -> {
			eb.send(EventSourceAddress.getWordAll(), "", reply -> {
				Message msg = reply.result();
				String st = (String) msg.body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(st);
			});
		};
	}

	private Handler<RoutingContext> queryWordHandler() {
		return rc -> {
			String id = rc.request().getParam("id");
			eb.send(EventSourceAddress.getWordQuery(), id, reply -> {
				Message msg = reply.result();
				Integer cnt = (Integer) msg.body();//(Integer) msg..body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(new JsonObject().put(id, cnt).encode());
			});
		};
	}

	private Handler<RoutingContext> queryEventHandler() {
		return rc -> {
			String id = rc.request().getParam("id");
			eb.send(EventSourceAddress.getEventQuery(), id, reply -> {
				Message msg = reply.result();
				Integer cnt = (Integer) msg.body();//(Integer) msg..body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(new JsonObject().put(id, cnt).encode());
			});
		};
	}

}