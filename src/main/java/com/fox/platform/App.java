package com.fox.platform;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fox.platform.vrt.CircuitBreakerTestVerticle;
import com.fox.platform.vrt.EndpointVerticle;
import com.fox.platform.vrt.PingVerticle;
import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

/**
 * Hello world!
 *
 */
public class App {

	private Logger logger = LoggerFactory.getLogger(App.class);
	
	private static final String VERTX_LOGGING_DELEGATE = "vertx.logger-delegate-factory-class-name"; 
	private static final String VERTX_CONFIG_FILE = "vertx.config-file";
	private static final String VERTX_CONFIG_URL = "vertx.config-url";
	private static final String LOG4J_CONFIG_FILE = "log4j.configurationFile";
	private static final String HAZELCAST_CLUSTER_CONFIG_FILE = "hazelcast.cluster-config-file";
	private static final String HAZELCAST_LOGGING_TYPE = "hazelcast.logging.type";
	
	private static final String VERTX_OPTIONS_CONFIG_FIELD = "vertxOptions";
	private static final String DEFAULT_CONFIG_FILE = "/config.json";
	
	
	
	
	public void startApp() {
		
		
		loadSystemProperties();

		String clusterConfigPath = System.getProperty(HAZELCAST_CLUSTER_CONFIG_FILE);			
		ClusterManager clusterManager = getClusterManager(clusterConfigPath);
		
		
		
		JsonObject config = loadConfig();
		
		
		VertxOptions options = new VertxOptions(config.getJsonObject(VERTX_OPTIONS_CONFIG_FIELD,new JsonObject())).setClusterManager(clusterManager);

		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				
				if(logger.isDebugEnabled()){
					logger.debug("it has launched vertx cluster with config: [" + clusterConfigPath + "]");
				}
				Vertx vertx = res.result();
				DeploymentOptions deployOptions = new DeploymentOptions().setConfig(config);
				deployVerticle(vertx, deployOptions);				
				
			} else {
				logger.error("Error during vertx cluster initialization ", res.cause());
			}
		});
	
	}
	
	
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
	
	private void loadSystemPropertyVertxConfigFile(){
		String vertxConfigFile = System.getenv(VERTX_CONFIG_FILE);
		if(vertxConfigFile != null) {
			System.setProperty(VERTX_CONFIG_FILE, vertxConfigFile);
		}
	}
	
	private void loadSystemPropertyVertxConfigURL(){
		String vertxConfigFile = System.getenv(VERTX_CONFIG_URL);
		if(vertxConfigFile != null) {
			System.setProperty(VERTX_CONFIG_URL, vertxConfigFile);
		}
	}
	
	private JsonObject loadConfig() {
		// first try to get config from URL if not then load from file
		JsonObject config = loadConfigFromURL();
		if(config == null){
			String configFile = System.getProperty(VERTX_CONFIG_FILE);
			return loadConfigFromFile(configFile);
		} else {
			return config;
		}
		
	}
	
	private JsonObject loadConfigFromURL() {
		
		String configURL = System.getProperty(VERTX_CONFIG_URL);
		if(configURL == null){
			return null;
		}
		
		try{
			URL url = new URL(configURL);
			InputStream in = url.openStream();
			return new JsonObject(IOUtils.toString(in, Charset.forName("UTF-8")));
		} catch(Exception ex){			
			logger.error("Error when try to load the config from url: " + configURL + " then load config from file or default",ex);
			return null;
		}
		
	}
	
	
	private JsonObject loadConfigFromFile(String configPath){
		
		try{
			String configString = null;
			if(configPath == null){
				configString = loadConfigFileFromClasspath();
			} else {
				configString = loadConfigFile(configPath);
			}
			
			if(configString != null && FilenameUtils.getExtension(configString).equals("yaml")){
				return loadConfigFromYaml(configString);
			} else {
				return loadConfigFromJson(configString);
			}
		} catch(Exception ex){
			logger.error("Load Config Error, use Default config: " + ex.getMessage(), ex);
			return new JsonObject();
		}
		
		
	}
	
	private JsonObject loadConfigFromJson(String content) {
		try{
			return new JsonObject(content);
		} catch(Exception ex){
			logger.error("Load Config from JSON File Error, use Default config: " + ex.getMessage(), ex);
			return new JsonObject();
		}
	}
	

	private JsonObject loadConfigFromYaml(String content) {
		try{
			ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
			
			Map<String,Object> map = mapper.readValue(
					content, 
					new TypeReference<Map<String, Object>>() {});
			
			return new JsonObject(map);
			
		} catch(Exception ex){
			logger.error("Load Config from YAML File Error, use Default config: " + ex.getMessage(), ex);
			return new JsonObject();
		}
		
	}
	
	private String loadConfigFile(String configPath) throws Exception {
		
		Path path = Paths.get(configPath);
		if(Files.exists(path)){
			
			return new String(Files.readAllBytes(path),"UTF-8");
			
		} else {
			if(logger.isInfoEnabled()){
				logger.info("Not found config file, use default configuration");
			}
			return "";
		}
	}


	private String loadConfigFileFromClasspath() throws Exception{
		InputStream in = App.class.getResourceAsStream(DEFAULT_CONFIG_FILE); 
		if(in != null){
			if(logger.isInfoEnabled()){
				logger.info("Load Config File from the classpath");
			}
			
			return IOUtils.toString(in, "UTF-8");
		} else {
			if(logger.isInfoEnabled()){
				logger.info("Not found config file in classpath, use default configuration");
			}
			return "";
		}
	}


	private void loadSystemProperties(){
		
		
		loadSystemPropertyVertexLoggingDelegate();
		loadSystemPropertyLog4jConfigFile();
		loadSystemPropertyHazelcastConfigFile();
		loadSystemPropertyHazelcastLoggingType();
		loadSystemPropertyVertxConfigURL();
		loadSystemPropertyVertxConfigFile();
		
		
		
	}
	
	private void deployVerticle(Vertx vertx, DeploymentOptions deploymentOptions){
		
		
		DeploymentOptions testOptions = new DeploymentOptions(deploymentOptions).setInstances(5);
		vertx.deployVerticle(CircuitBreakerTestVerticle.class.getName(),testOptions);
		vertx.deployVerticle(EndpointVerticle.class.getName(),deploymentOptions);
		vertx.deployVerticle(PingVerticle.class.getName(),deploymentOptions);
		
	}

	public static void main(String[] args) {
		App app = new App();
		app.startApp();
	}
}
