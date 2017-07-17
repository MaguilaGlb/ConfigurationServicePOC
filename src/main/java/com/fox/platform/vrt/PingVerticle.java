package com.fox.platform.vrt;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class PingVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		vertx.setPeriodic(1000, handler -> {
			System.out.println("Sending Ping ...");
			vertx.eventBus().<JsonObject>send(CircuitBreakerTestVerticle.ADDRESS, "Ping ...", reply -> {
				if(reply.succeeded()){
					System.out.println("\t" + reply.result().body().encode());
				} else {
					System.err.println("\t" + reply.cause().getMessage());
				}
			});
		});
		
		
		super.start(startFuture);
		
		
	}
	
}
