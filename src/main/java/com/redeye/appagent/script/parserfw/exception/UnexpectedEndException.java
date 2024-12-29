package com.redeye.appagent.script.parserfw.exception;

import lombok.Getter;

/**
 * 파싱 수행시 완료상태가 아닌데 문자열이 끝난 경우 예외 클래스
 * 
 * @author jmsohn
 */
public class UnexpectedEndException extends Exception {

	/** Serializable GUID */
	private static final long serialVersionUID = 9217861306987755508L;
	
	/** 예외 발생 위치 */
	@Getter
	private int pos;
	
	/** 예외 발생 상태 */
	@Getter
	private String status;
	
	/**
	 * 생성자
	 * 
	 * @param pos 예외 발생 위치
	 * @param status 예외 발생 상태
	 */
	public UnexpectedEndException(int pos, String status) {
		
		super(toString(pos, status));
		
		this.pos = pos;
		this.status = status;
	}
	
	/**
	 * 예외 정보에 대해 문자열로 변환
	 * 
	 * @param pos 예외 발생 위치
	 * @param status 예외 발생 상태
	 * @return 변환된 문자열
	 */
	private static String toString(int pos, String status) {
		return "Unexpected end status: " + status + " at " + pos;
	}
	
	@Override
	public String toString() {
		return toString(this.pos, this.status);
	}
}
