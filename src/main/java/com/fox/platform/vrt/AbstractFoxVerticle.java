package com.fox.platform.vrt;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class AbstractFoxVerticle extends AbstractVerticle {
	
	public static final String CONFIG_RETRIVER_OPTIONS_CONFIG_FIELD = "configRetrieverOptions";
	
	public static final String UPDATE_CONFIG_ADDRESS = "updateConfigAddress";
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private ConfigRetriever configRetriever;
	
	private JsonObject config = null;
	
	@Override
	public void start(Future<Void> startFuture) throws Exception {
		
		configRetriever = createConfigRetriever(config().getJsonObject(CONFIG_RETRIVER_OPTIONS_CONFIG_FIELD, new JsonObject())); 
		
		vertx.eventBus().<Void>consumer(UPDATE_CONFIG_ADDRESS, handler -> updateConfig(configRetriever));
		
		super.start(startFuture);
	}
	
	@Override
	public JsonObject config(){
		
		if(config == null){
			config = super.config();
		}
		
		return config;
		
	}
	
	public void configChange(JsonObject newConfig, JsonObject oldConfig){
		
		//if the config retirever change then close and create new
		JsonObject newConfgiRetreiver = newConfig.getJsonObject(CONFIG_RETRIVER_OPTIONS_CONFIG_FIELD,new JsonObject());
		if(! config.equals(newConfig)){
			configRetriever.close();
			configRetriever = createConfigRetriever(newConfgiRetreiver);
		}
		
		
	}
	
	private ConfigRetriever createConfigRetriever(JsonObject configRetrieverJson) {
		
		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions(configRetrieverJson);
		
		logger.info(super.deploymentID() + " Create a new Config Retriver with options: " + configRetrieverOptions.toJson().encode());
		
		ConfigRetriever newConfigRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);
		
		updateConfig(newConfigRetriever);
		newConfigRetriever.listen(change -> {
			configChange(change.getNewConfiguration(), change.getPreviousConfiguration());
			config.mergeIn(change.getNewConfiguration());
		});
		
		return newConfigRetriever;
	}
	
	private void updateConfig(ConfigRetriever configRetrieverToUpdate){
		configRetrieverToUpdate.getConfig(json -> {
			if(json.succeeded()){
				config = json.result();
			} else {
				logger.error(super.deploymentID() + " Error in ConfigRetriever to load config: " + json.cause().getMessage(), json.cause());
			}
		});
	}

}
