package com.foxsports.afa;

import java.net.ServerSocket;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.fox.platform.blueprint.App;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;


@RunWith(VertxUnitRunner.class)
public class AppTest {
	
	private Vertx vertx;
	
	private int port;

	@Before
	public void setUp(TestContext context) {
		
		try{
			ServerSocket socket = new ServerSocket(0);
			port = socket.getLocalPort();
			socket.close();
		} catch(Exception ex){
			port = 8080;
		}

		DeploymentOptions options = new DeploymentOptions()
		    .setConfig(new JsonObject().put("http.port", port)
		    );
		
		vertx = Vertx.vertx();
		
		App app = new App();
		app.startVertxApp(vertx, options);
		
	}

	@After
	public void tearDown(TestContext context) {
		vertx.close(context.asyncAssertSuccess());
	}

	@Test
	public void testMyApplication(TestContext context) {
		final Async async = context.async();

		vertx.createHttpClient().getNow(port, "localhost", "/", response -> {
			response.handler(body -> {
				context.assertTrue(body.toString().contains("Hello"));
				async.complete();
			});
		});
	}
}
