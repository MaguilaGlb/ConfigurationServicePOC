package com.fox.platform.vo;

public class Endpoint {
	
	private int port;
	private String host;
	private String path;
	private boolean ssl;
	
	public Endpoint() {
		super();
	}
	
	public Endpoint(int port, String host, String path, boolean ssl) {
		this();
		this.port = port;
		this.host = host;
		this.path = path;
		this.ssl = ssl;
	}
	
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public boolean isSsl() {
		return ssl;
	}
	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
	

}

