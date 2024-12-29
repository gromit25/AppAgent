package com.redeye.appagent.script.parserfw.exception;

import lombok.Getter;

/**
 * 파싱 수행시 예외 클래스
 * 
 * @author jmsohn
 */
public class ParseException extends Exception {

	/** Serializable GUID */
	private static final long serialVersionUID = -5962084713002262234L;
	
	/** 예외 발생 위치 */
	@Getter
	private int pos;
	
	/** 예외 발생 문자 */
	@Getter
	private char ch;
	
	/** 예외 발생 상태 */
	@Getter
	private String status;
	
	/**
	 * 생성자
	 * 
	 * @param pos 예외 발생 위치
	 * @param ch 예외 발생 문자
	 * @param status 예외 발생 상태
	 */
	public ParseException(int pos, char ch, String status) {
		
		super(toString(pos, ch, status));
		
		this.pos = pos;
		this.ch = ch;
		this.status = status;
	}
	
	/**
	 * 예외 정보에 대해 문자열로 변환
	 * 
	 * @param pos 예외 발생 위치
	 * @param ch 예외 발생 문자
	 * @param status 예외 발생 상태
	 * @return 변환된 문자열
	 */
	private static String toString(int pos, char ch, String status) {
		return "Unexpected char: " + ch + ", status:" + status + " at " + pos;
	}
	
	@Override
	public String toString() {
		return toString(this.pos, this.ch, this.status);
	}
}
