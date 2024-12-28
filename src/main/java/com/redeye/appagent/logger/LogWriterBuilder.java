package com.redeye.appagent.logger;

import com.redeye.appagent.logger.file.FileWriter;
import com.redeye.appagent.util.StringUtil;

/**
 * 로그 Writer 빌더
 * 
 * @author jmsohn
 */
class LogWriterBuilder {
	
	/** 생성할 로그 Writer 타입명 */
	private String type;
	
	/** 로그 Writer 타입명이 유효한 지 여부 */
	private boolean isValidType;
	
	/**
	 * 생성자
	 * 
	 * @param type 로그 Writer 타입 명
	 */
	LogWriterBuilder(String type) {
		
		// 로그 Writer 타입 설정
		this.type = type;
		
		// 타입 명이 유효한지 여부 설정
		this.isValidType = (StringUtil.isBlank(this.type) == false);
	}
	
	/**
	 * 로그 Writer 타입 명에 따라 로그 Writer 생성 및 반환<br>
	 * 로그 Writer 타입 명이 유효하지 않거나 일치하는 로그 Writer 가 없으면 null 을 반환
	 * 
	 * @return 생성된 로그 Writer
	 */
	LogWriter create() {
		
		// 타입 명이 유효하지 않을 경우 null 반환
		if(this.isValidType == false) {
			return null;
		}
		
		// 타입 별로 로그 Writer 객체를 생성하여 반환
		if(this.type.equals("FILE") == true) {
			
			// 파일 형 로그 Writer
			return new FileWriter();
		}
		
		return null;
	}
}
