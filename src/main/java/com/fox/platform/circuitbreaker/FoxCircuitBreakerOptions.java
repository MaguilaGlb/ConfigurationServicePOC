package com.fox.platform.circuitbreaker;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class FoxCircuitBreakerOptions extends io.vertx.circuitbreaker.CircuitBreakerOptions {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final boolean DEFAULT_IS_FORCED_OPEN = false;

	private boolean forceOpen;

	

	public boolean isForceOpen() {
		return forceOpen;
	}



	public void setForceOpen(boolean forceOpen) {
		this.forceOpen = forceOpen;
	}



	@Override
	public long getResetTimeout() {
		if(forceOpen){
			if(logger.isDebugEnabled()) logger.debug("getResetTimeout Forced Open return -1");
			return -1L;
		} else {
			if(logger.isDebugEnabled()) logger.debug("getResetTimeout return " + super.getResetTimeout());
			return super.getResetTimeout();
		}		
	}

}
