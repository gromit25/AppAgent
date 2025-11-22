package com.redeye.appagent.logger;

import java.util.Map;

/**
 * Logfmt 유틸리티 클래스
 * 
 * @author jmsohn
 */
public class Logfmt {
	
	/**
	 * Logfmt 로그 문자열로 출력
	 * 
	 * @param map 변환할 맵
	 * @return 로그 문자열
	 */
	public static String toString(Map<String, Object> map) {
		
		StringBuilder builder = new StringBuilder("");
		
		for(String key: map.keySet()) {
			
			if(builder.length() != 0) {
				builder.append(" ");
			}
			
			// key 추가
			builder
				.append(key)
				.append("=");
			
			// value 추가
			String value = map.get(key).toString();
			
			// 문자열 내부에 스페이스 문자가 포함된 경우 따옴표(") 추가
			if(value.matches(".*[\\s\"].*") == true) {
				
				value = value.replace("\"", "\\\"");
				
				builder
					.append("\"")
					.append(value)
					.append("\"");
				
			} else {
				builder.append(value);
			}
		}
		
		return builder.toString();
	}
}
