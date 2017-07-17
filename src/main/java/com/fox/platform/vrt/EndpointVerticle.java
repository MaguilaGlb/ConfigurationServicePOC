package com.fox.platform.vrt;

import java.util.Date;
import java.util.UUID;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fox.platform.vo.RequestObject;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.MimeMapping;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;


public class EndpointVerticle extends AbstractVerticle {
	
	private Logger logger = LoggerFactory.getLogger(EndpointVerticle.class);
	
	private String id = UUID.randomUUID().toString();
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		if(logger.isDebugEnabled()){
			logger.debug("Start Http Server at port: " + config().getInteger("http.port", 8080));			
		}
		
		Router router = Router.router(vertx);
		router.get("/updateConfig").handler(this::handleUpdateConfig);
		router.route().handler(this::handleOthers);
		
		
		int port = config().getJsonObject("baseLine",new JsonObject()).getInteger("port",8080);
		
		
		vertx.createHttpServer().requestHandler(router::accept).listen(port,
				result -> {

					if (result.succeeded()) {
						startFuture.complete();
					} else {
						startFuture.fail(result.cause());
					}

				});

	}
	
	private void handleUpdateConfig(RoutingContext routingContext) {
		vertx.eventBus().<Void>publish(AbstractFoxVerticle.UPDATE_CONFIG_ADDRESS, null);
		
		HttpServerResponse response = routingContext.response();
		
		response
			.setStatusCode(HttpResponseStatus.OK.code()) 
			.putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("txt"))
			.end("OK");
	}
	
	private void handleOthers(RoutingContext routingContext) {
		
		HttpServerResponse response = routingContext.response();		
		RequestObject requestObject = new RequestObject(id, new Date());
		vertx.eventBus().<JsonObject>send(CircuitBreakerTestVerticle.ADDRESS, JsonObject.mapFrom(requestObject)  , reply -> {
			if(reply.succeeded()){
				JsonObject responseObject = reply.result().body();
				response
					.setStatusCode(HttpResponseStatus.OK.code()) 
					.putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("json"))
					.end(responseObject.encode());
				
			} else {
				response
				.setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code()) 
				.putHeader(HttpHeaders.CONTENT_TYPE, MimeMapping.getMimeTypeForExtension("txt"))
				.end(ExceptionUtils.getStackTrace(reply.cause()));
			}
			
		});
	}

}
