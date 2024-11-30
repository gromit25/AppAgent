package com.redeye.babe.agent.exception;

/**
 * Agent 에서 발생하는 예외
 * 
 * @author jmsohn
 */
public final class AgentException extends Exception {

	private static final long serialVersionUID = 7686778943106112750L;

	/**
	 * 생성자
	 */
	public AgentException() {
		super();
	}
	
	/**
	 * 생성자
	 * 
	 * @param msg 예외 메시지
	 */
	public AgentException(String msg) {
		super(msg);
	}
}
