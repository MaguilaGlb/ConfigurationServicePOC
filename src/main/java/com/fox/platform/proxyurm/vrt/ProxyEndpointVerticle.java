package com.fox.platform.proxyurm.vrt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

public class ProxyEndpointVerticle extends AbstractVerticle {
	
	private Logger logger = LoggerFactory.getLogger(ProxyEndpointVerticle.class);

	public final static String BASE_PATH = "/api/proxycrm/";

	public final static String HAS_ACCESS = "hasAccess";	

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		if(logger.isDebugEnabled()){
			logger.debug("Start Http Server at port: " + config().getInteger("http.port", 8080));			
		}
		
		Router router = Router.router(vertx);

		
		router.get(BASE_PATH + HAS_ACCESS).handler(this::handleHasAccess);		
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
		HttpServerResponse response = routingContext.response();
		response
			.setStatusCode(404) // Not Found
			.putHeader("content-type", "text/plain")
			.end("ProxyCRM API");
	}
	
	private void handleHasAccess(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		sendNotImplementedError(response);
	}
	
	
	private void sendNotImplementedError(HttpServerResponse response){
		response
			.setStatusCode(501) // Not Implemented
			.putHeader("content-type", "text/plain")
			.end("ProxyCRM API");
	}

}
