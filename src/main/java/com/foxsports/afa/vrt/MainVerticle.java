package com.foxsports.afa.vrt;

import org.slf4j.Logger;

import com.foxsports.afa.App;
import com.foxsports.afa.add.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;

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
		
		
		if(logger.isDebugEnabled()) logger.debug("Procesando solicitud host: " + request.host() + " path: " + request.path() + " query: " + request.query());
		
		String mensaje = this.getClass().getName();
		
		vertx
			.eventBus()
			.send(Address.HELLOWORLD_ADD, mensaje, reply -> {
				
				if(logger.isDebugEnabled()) logger.debug("Recibiendo respuesta mensaje succeeded: " + reply.succeeded() + " body: " + reply.result().body());
				
				if(reply.succeeded()){
					request
						.response()						
						.end("<h1>" + reply.result().body() + "</h1>");
				}
			});
		
		
	}

	
	
}
