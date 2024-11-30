package com.redeye.babe.log;

public enum ApiType {
	DB("DB"),
	TCP_SOCKET("TCP"),
	UDP_SOCKET("UDP"),
	FILE("FILE"),
	AGENT("AGENT"),
	NONE("NONE");
	
	private String apiTypeName;
	
	private ApiType(String apiTypeName) {
		this.apiTypeName = apiTypeName;
	}

	public String getApiTypeName() {
		return this.apiTypeName;
	}
}
