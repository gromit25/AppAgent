package com.redeye.appagent.log;

import lombok.Getter;

/**
 * 호출 API 종류
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
	
	// -------------------------------
	
	/** 호출 API 명 */
	@Getter
	private String name;
	
	/**
	 * 생성자
	 * 
	 * @param name 호출 API 명
	 */
	private ApiType(String name) {
		this.name = name;
	}
}
