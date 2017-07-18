package com.fox.platform.vrt;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public abstract class AbstractFoxVerticle extends AbstractVerticle {
	
	public static final String CONFIG_RETRIVER_OPTIONS_CONFIG_FIELD = "configRetrieverOptions";
	
	public static final String REQUEST_TO_UPDATE_CONFIG_ADDRESS = "requestToupdateConfigAddress";
	
	public static final String UPDATE_CONFIG_ADDRESS = "updateConfigAddress";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	
	private JsonObject config = null;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		vertx.eventBus().<JsonObject>consumer(UPDATE_CONFIG_ADDRESS, this::updateConfig);
		super.start(startFuture);
	}
	
	@Override
	public JsonObject config(){
		
		if(config == null){
			config = super.config();
		}
		
		return config;
		
	}
	
	public abstract void configChange(JsonObject newConfig, JsonObject oldConfig);
	
	
	
	private void updateConfig(Message<JsonObject> configMessage){
		JsonObject newConfig = configMessage.body();
		configChange(newConfig, config);
		config = newConfig;
	}

}
