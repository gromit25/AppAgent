package com.redeye.appagent.transform;

import java.util.HashMap;
import java.util.Map;

import com.redeye.appagent.exception.AgentException;

/**
 * 메소드 변환 Map
 * 
 * @author jmsohn
 */
public class MethodMap {
	
	/** 메소드 변환 Map 객체 */
	private static Map<String, MethodPair> map;
	
	/**
	 * Method Map 초기화
	 * 
	 * @param targetClasses
	 */
	public static void init(Class<?>... targetClasses) throws Exception {
		
		// 입력값 검증
		if(targetClasses == null) {
			throw new AgentException("target classes is null.");
		}
		
		// Method Map 객체 생성
		map = new HashMap<>();
		
		// 각 클래스 로드
		for(Class<?> targetClass: targetClasses) {
			
			for(MethodPair methodPair: MethodPair.load(targetClass)) {
				map.put(methodPair.getKey(), methodPair);
			}
		}
	}
}
