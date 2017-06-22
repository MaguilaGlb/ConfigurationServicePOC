package com.fox.platform.vrt;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProxyEndpointVerticle extends AbstractVerticle {
	
	private Logger logger = LoggerFactory.getLogger(ProxyEndpointVerticle.class);
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		if(logger.isDebugEnabled()){
			logger.debug("Start Http Server at port: " + config().getInteger("http.port", 8080));			
		}
		
		Router router = Router.router(vertx);
				
		router.route().handler(this::handleOthers);
		

		vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080),
				result -> {

					if (result.succeeded()) {
						startFuture.complete();
					} else {
						startFuture.fail(result.cause());
					}

				});

	}

	private void handleOthers(RoutingContext routingContext) {
		
		
		JsonObject result = new JsonObject();
		result.put("app", "BaselineVertx");
		result.put("version", "0.0.1");
		result.put("now", LocalDateTime.now().toString());
		
		
		
		HttpServerResponse response = routingContext.response();
		response
			.setStatusCode(200) 
			.putHeader("content-type", "application/json; charset=utf-8")
			.end(result.encode());
	}

}
