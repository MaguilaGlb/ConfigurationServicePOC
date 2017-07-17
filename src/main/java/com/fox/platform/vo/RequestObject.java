package com.fox.platform.vo;

import java.util.Date;

public class RequestObject {
	
	private String id;
	private Date fecha;
	
	public RequestObject(String id, Date fecha) {
		super();
		this.id = id;
		this.fecha = fecha;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	

}
