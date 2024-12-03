package com.redeye.babe.log;

/**
 * 
 * 
 * @author jmsohn
 */
public enum ApiType {
	
	AGENT("AGENT"),
	DB("DB"),
	TCP_SOCKET("TCP"),
	UDP_SOCKET("UDP"),
	FILE("FILE"),
	NONE("NONE");
	
	private String name;
	
	private ApiType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
