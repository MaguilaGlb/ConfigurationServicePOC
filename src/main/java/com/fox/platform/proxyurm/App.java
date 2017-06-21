package com.fox.platform.proxyurm;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fox.platform.proxyurm.vrt.ProxyEndpointVerticle;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Hello world!
 *
 */
public class App {

	private Logger logger = LoggerFactory.getLogger(App.class);
	
	private static final String VERTX_LOGGING_DELEGATE = "vertx.logger-delegate-factory-class-name"; 
	private static final String LOG4J_CONFIG_FILE = "log4j.configurationFile";
	private static final String HAZELCAST_CLUSTER_CONFIG_FILE = "hazelcast.cluster-config-file";
	private static final String HAZELCAST_LOGGING_TYPE = "hazelcast.logging.type";
	
	
	private boolean existsClusterConfig(String clusterConfigPath){
		if(clusterConfigPath != null && ! clusterConfigPath.isEmpty()){
			Path configPath = Paths.get(clusterConfigPath);
			return Files.exists(configPath);
		} else {
			return false;
		}
	}
	
	private ClusterManager getClusterManager(String clusterConfigPath) {
		
		Config hcConfiguration = null;
		if(clusterConfigPath == null){
			if(logger.isDebugEnabled())
				logger.debug("Load Hazelcast Configuration File from the classpath");			
			InputStream in = App.class.getResourceAsStream("/cluster.xml");
			if(in == null){
				hcConfiguration = new XmlConfigBuilder(in).build();
			} 
		} else {
			if (existsClusterConfig(clusterConfigPath)) {
				
				try{
					if(logger.isDebugEnabled())
						logger.debug("Load Hazelcast Configuration File from the file: " + clusterConfigPath);					
					hcConfiguration = new XmlConfigBuilder(clusterConfigPath).build();
				} catch(FileNotFoundException ex){
					logger.error("I'll be never happend ... Error to load hazelcast config from file: " + clusterConfigPath, ex);
				}
			}
		}
		
		if(hcConfiguration != null){
			return new HazelcastClusterManager(hcConfiguration);
		} else {
			if(logger.isDebugEnabled())
				logger.debug("Return empty hazelcast config");
			
			return new HazelcastClusterManager();
		}
		
	}
	
	
	private void loadSystemPropertyVertexLoggingDelegate(){
		String vertxLogDelegate = System.getenv(VERTX_LOGGING_DELEGATE);
		if(vertxLogDelegate == null){
			vertxLogDelegate = System.getProperty(VERTX_LOGGING_DELEGATE);
			if(vertxLogDelegate == null){
				//default value
				System.setProperty(VERTX_LOGGING_DELEGATE, "io.vertx.core.logging.SLF4JLogDelegateFactory");
			}
		} else {
			System.setProperty(VERTX_LOGGING_DELEGATE, vertxLogDelegate);
		}
	}
	
	private void loadSystemPropertyLog4jConfigFile(){
		String log4jConfigFile = System.getenv(LOG4J_CONFIG_FILE);
		if(log4jConfigFile != null) {
			System.setProperty(LOG4J_CONFIG_FILE, log4jConfigFile);
		}
	}
	
	private void loadSystemPropertyHazelcastConfigFile(){
		String hazelcastConfigFile = System.getenv(HAZELCAST_CLUSTER_CONFIG_FILE);
		if(hazelcastConfigFile != null) {
			System.setProperty(HAZELCAST_CLUSTER_CONFIG_FILE, hazelcastConfigFile);
		}
	}
	
	private void loadSystemPropertyHazelcastLoggingType(){
		String hazelcastLoggingType = System.getenv(HAZELCAST_LOGGING_TYPE);
		if(hazelcastLoggingType == null){
			hazelcastLoggingType = System.getProperty(HAZELCAST_LOGGING_TYPE);
			if(hazelcastLoggingType == null){
				//default value
				System.setProperty(HAZELCAST_LOGGING_TYPE, "slf4j");
			}
		} else {
			System.setProperty(HAZELCAST_LOGGING_TYPE, hazelcastLoggingType);
		}
	}
	
	private void loadSystemProperties(){
		
		
		loadSystemPropertyVertexLoggingDelegate();
		loadSystemPropertyLog4jConfigFile();
		loadSystemPropertyHazelcastConfigFile();
		loadSystemPropertyHazelcastLoggingType();
		
		
	}
	
	public void startApp() {
			
		
			loadSystemProperties();

			String clusterConfigPath = System.getProperty(HAZELCAST_CLUSTER_CONFIG_FILE);			
			ClusterManager clusterManager = getClusterManager(clusterConfigPath);
			

			VertxOptions options = new VertxOptions().setClusterManager(clusterManager);

			Vertx.clusteredVertx(options, res -> {
				if (res.succeeded()) {
					
					if(logger.isDebugEnabled()){
						logger.debug("it has launched vertx cluster with config: [" + clusterConfigPath + "]");
					}
					Vertx vertx = res.result();
					
					vertx.deployVerticle(ProxyEndpointVerticle.class.getName());
				} else {
					logger.error("Error al lanzar cluster vertx ", res.cause());
				}
			});
		
	}

	public static void main(String[] args) {
		App app = new App();
		app.startApp();
	}
}
