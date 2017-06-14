package com.foxsports.afa.vrt;

import org.slf4j.Logger;

import com.foxsports.afa.App;
import com.foxsports.afa.add.Address;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

public class HelloWorldVerticle extends AbstractVerticle {
	
	private Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);
	
	
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		if(logger.isDebugEnabled()) logger.debug("Start: " + this.getClass().getName());
		
		vertx.eventBus().consumer(Address.HELLOWORLD_ADD, this::sendGreeting);	
		startFuture.complete();
	}

	
	private void sendGreeting(Message<String> request){		
		
		if(logger.isDebugEnabled()) logger.debug("Response a request of: " + request.body());
		
		String greeting = "Hello World to " + request.body() + " !!! X";		
		request.reply(greeting);
	}
	
}
