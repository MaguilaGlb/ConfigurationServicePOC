package com.fox.platform.blueprint.vrt;

import org.slf4j.Logger;

import com.fox.platform.blueprint.App;
import com.fox.platform.blueprint.add.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class HelloWorldVerticle extends AbstractVerticle {
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
	
	
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		if(logger.isDebugEnabled()) logger.debug("Start: " + this.getClass().getName());
		
		vertx.eventBus().consumer(Address.HELLOWORLD_ADD, this::sendGreeting);	
		startFuture.complete();
	}

	
	private void sendGreeting(Message<JsonObject> request){		
		
		if(logger.isDebugEnabled()) logger.debug("Response a request of: " + request.body());
		
		JsonObject requestMessage = (JsonObject) request.body();
		
		JsonObject responseObject = new JsonObject();
		responseObject.put("verticle-from", requestMessage.getString("verticle"));
		responseObject.put("date-from", requestMessage.getLong("date"));
		responseObject.put("verticle-to", this.getClass().getName());
		responseObject.put("date-to", System.currentTimeMillis());
		
				
		request.reply(responseObject);
	}
	
}
