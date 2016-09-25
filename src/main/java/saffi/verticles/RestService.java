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


public class RestService  extends AbstractVerticle {
		private EventBus eb;
		@Override
		public void start(Future<Void> started) {
			eb=vertx.eventBus();

			Future<HttpServer> serverFuture = Future.future();
			serverFuture.setHandler(
					hs->{
						started.complete();
					});

			Router router = setRoutes();

			vertx.createHttpServer().requestHandler(router::accept).listen(
					getPort(),
					serverFuture.completer() );
		}

	public Router setRoutes() {
		Router router = Router.router(vertx);

		router.get("/words").handler(allWordHandler());
		router.get("/events").handler(allTypeHandler());
		router.get("/word/:id").handler(queryWordHandler());
		router.get("/event/:id").handler(queryEventHandler());
		return router;
	}

	private Handler<RoutingContext> allTypeHandler() {
		return rc -> {
			eb.send(EventSourceAddress.getEventAll(), "", reply->{
				Message msg = reply.result();
				String st= (String) msg.body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(st);
			});
		};
	}

	private Handler<RoutingContext> allWordHandler() {
		return rc -> {
			eb.send(EventSourceAddress.getWordAll(), "", reply->{
				Message msg = reply.result();
				String st= (String) msg.body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(st);
			});
		};
	}

	private Handler<RoutingContext> queryWordHandler() {
		return rc -> {
			String id = rc.request().getParam("id");
			eb.send(EventSourceAddress.getWordQuery(), id, reply->{
				Message msg = reply.result();
				Integer cnt= (Integer) msg.body();//(Integer) msg..body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(new JsonObject().put(id, cnt).encode());
			});
		};
	}

	private Handler<RoutingContext> queryEventHandler() {
		return rc -> {
			String id = rc.request().getParam("id");
			eb.send(EventSourceAddress.getEventQuery(), id, reply->{
				Message msg = reply.result();
				Integer cnt= (Integer) msg.body();//(Integer) msg..body();
				rc.response()
						.putHeader("content-type", "application/json")
						.end(new JsonObject().put(id, cnt).encode());
			});
		};
	}

	public Integer getPort() {
		// Port is specified in the conf file
		// fallback for conf :
		final int PORT_DEFAULT = 8080;
		return config().getInteger("http.port", PORT_DEFAULT);
	}
}