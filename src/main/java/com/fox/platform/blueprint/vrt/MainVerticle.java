package com.fox.platform.blueprint.vrt;

import java.time.LocalDateTime;

import org.slf4j.Logger;

import com.fox.platform.blueprint.App;
import com.fox.platform.blueprint.add.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends AbstractVerticle {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		if(logger.isDebugEnabled()) logger.debug("Start " + this.getClass().getName());
		
		vertx
			.createHttpServer()
			.requestHandler(this::requestProcess)
			.listen(config().getInteger("http.port",8080), result -> {
				
				if(result.succeeded()){
					startFuture.complete();
				} else {
					startFuture.fail(result.cause());
				}
				
			});
		
		
	}
	
	private void requestProcess(HttpServerRequest request){
		
		
		if(logger.isDebugEnabled()) logger.debug("Request Process host: " + request.host() + " path: " + request.path() + " query: " + request.query());
		
		
		
		JsonObject requestMessage = new JsonObject();
		requestMessage.put("verticle", this.getClass().getName());
		requestMessage.put("date", System.currentTimeMillis());
		
		
		vertx
			.eventBus()
			.send(Address.HELLOWORLD_ADD, requestMessage, reply -> {
				
				if(logger.isDebugEnabled()) logger.debug("Receive answer succeeded: " + reply.succeeded() + " body: " + reply.result().body());
				
				if(reply.succeeded()){
					request
						.response()	
						.putHeader("content-type", "application/json; charset=utf-8")
						.end(Json.encodePrettily(reply.result().body()));
				}
			});
		
		
	}

	
	
}
