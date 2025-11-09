package com.redeye.appagent.logger;

import java.util.Map;

import com.redeye.appagent.util.StringUtil;

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
			
			String value = map.get(key).toString();
			
			if(StringUtil.hasBlank(value) == true) {
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
