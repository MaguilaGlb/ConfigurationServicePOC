package com.fox.platform.blueprint;

import org.slf4j.Logger;

import com.fox.platform.blueprint.vrt.HelloWorldVerticle;
import com.fox.platform.blueprint.vrt.MainVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Hello world!
 *
 */
public class App {

	Logger logger = org.slf4j.LoggerFactory.getLogger(App.class);

	public void startVertxApp() {

		Vertx vertx = Vertx.vertx();

		DeploymentOptions options = new DeploymentOptions().setConfig(new JsonObject());

		startVertxApp(vertx, options);

	}

	public void startVertxApp(Vertx vertx, DeploymentOptions options) {

		if (logger.isDebugEnabled())
			logger.debug("Deploy Verticle " + HelloWorldVerticle.class.getName());
		Future<String> futureHello = Future.future();
		vertx.deployVerticle(HelloWorldVerticle.class.getName(), options, futureHello.completer());

		futureHello.compose(v -> {
			if (logger.isDebugEnabled())
				logger.debug("Deploy Verticle " + MainVerticle.class.getName());
			Future<String> futureMain = Future.future();
			vertx.deployVerticle(MainVerticle.class.getName(), options, futureMain.completer());
			return futureMain;

		});
	}

	public static void main(String[] args) {
		App app = new App();
		app.startVertxApp();

	}
}
