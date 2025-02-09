package com.redeye.appagent.rewriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.redeye.appagent.exception.AgentException;
import com.redeye.appagent.util.StringUtil;

/**
 * 메소드 변환 Map
 * 
 * @author jmsohn
 */
public class MethodMap {
	
	/** 메소드 변환 Map 객체 */
	private static Map<String, MethodPair> map = new HashMap<>();
	
	/** 변환 대상 NEW 클래스 셋 */
	private static Set<String> targetNew = new HashSet<>();
	
	/**
	 * 변환 Map 초기화
	 * 
	 * @param targetClassesStr 변환 클래스 목록 문자열
	 */
	public static void init(String targetClassesStr) throws Exception {
		
		// 대상 클래스 목록이 없는 경우 반환
		if(StringUtil.isEmpty(targetClassesStr) == true) {
			return;
		}
		
		// 변환 클래스 목록 로딩
		List<Class<?>> targetClasses = new ArrayList<>();
		for(String targetClassStr: targetClassesStr.split("[ \\t]*//,[ \\t]*")) {
			
			Class<?> targetClass = Class.forName(targetClassStr);
			targetClasses.add(targetClass);
		}
		
		// 로딩된 클래스 목록으로 초기화 수행
		init(targetClasses);
	}
	
	/**
	 * Method Map 초기화
	 * 
	 * @param targetClasses
	 */
	public synchronized static void init(List<Class<?>> targetClasses) throws Exception {
		
		// 입력값 검증
		if(targetClasses == null) {
			throw new AgentException("target classes is null.");
		}
		
		// Method Map 초기화
		map.clear();
		
		// NEW 클래스 목록 초기화
		targetNew.clear();
		
		// 각 클래스별 변환 메소드 로드
		for(Class<?> targetClass: targetClasses) {
			
			for(MethodPair methodPair: MethodPair.load(targetClass)) {
				
				// Method Map에 저장
				map.put(methodPair.getKey(), methodPair);
				
				// 만일 대상 메소드 명이 생성자("<init>") 메소드 이면
				// NEW 클래스 목록에 추가
				if(methodPair.getTargetMethod().getMethodName().equals("<init>") == true) {
					targetNew.add(methodPair.getTargetMethod().getClassName());
				}
			}
		}
	}
	
	/**
	 * 주어진 메소드가 map 대상 여부 반환
	 * 
	 * @param className 클래스 명
	 * @param methodName 메소드 명
	 * @param signature 시그니처
	 * @return 주어진 메소드가 map 대상 여부
	 */
	public static boolean isTarget(
		String className,
		String methodName,
		String signature
	) {
		
		// class 명, method 명, signature 가 blank 인 경우 false 반환
		if(StringUtil.isBlank(className) == true) {
			return false;
		}
		
		if(StringUtil.isBlank(methodName) == true) {
			return false;
		}
		
		if(StringUtil.isBlank(signature) == true) {
			return false;
		}
		
		// map 에 있는지 여부 반환
		return map.containsKey(className + "." + methodName + signature);
	}
	
	/**
	 * 주어진 클래스 명의 변환 대상 여부 반환
	 * 
	 * @param className 클래스 명
	 * @return 변환 대상 여부
	 */
	public static boolean isTargetNew(String className) {
		
		// class 명이 blank 일 경우 false 반환
		if(StringUtil.isBlank(className) == true) {
			return false;
		}
		
		return targetNew.contains(className);
	}
	
	/**
	 * 주어진 대상 메소드 정보로 변환 메소드 짝을 반환<br>
	 * 없을 경우 null 을 반환
	 * 
	 * @param className 대상 클래스 명
	 * @param methodName 대상 메소드 명
	 * @param signature 대상 시그니처
	 * @return 변환 메소드 짝
	 */
	public static MethodPair getMethodPair(String className, String methodName, String signature) {
		
		// 입력값 검증
		if(StringUtil.isBlank(className) == true) {
			return null;
		}
		
		if(StringUtil.isBlank(methodName) == true) {
			return null;
		}

		if(StringUtil.isBlank(signature) == true) {
			return null;
		}
		
		// 맵이 초기화 되지 않았을 경우 null 반환
		if(map == null) {
			return null;
		}
		
		// 맵에서 메소드 변환 짝 정보를 반환
		return map.get(className + "." + methodName + signature);
	}
	
	/**
	 * 대상 메소드 스펙으로 변환할 메소드 스펙을 반환<br>
	 * 없을 경우 null 을 반환
	 * 
	 * @param targetSpec 대상 메소드 스펙
	 * @return 변환 메소드 스펙
	 */
	public static MethodSpec getAltMethod(MethodSpec targetSpec) {
		
		// 입력값 검증
		if(targetSpec == null) {
			return null;
		}
		
		// 맵이 초기화 되지 않았을 경우 null 반환
		if(map == null) {
			return null;
		}
		
		// 맵에서 변환할 메소드 스팩을 반환
		String key = targetSpec.toString();
		if(map.containsKey(key) == true) {
			return map.get(key).getAltMethod();
		} else {
			return null;
		}
	}
	
	/**
	 * 주어진 대상 메소드 정보로 변환할 메소드 스펙을 반환<br>
	 * 없을 경우 null 을 반환
	 * 
	 * @param className 대상 클래스 명
	 * @param methodName 대상 메소드 명
	 * @param signature 대상 시그니처
	 * @return 변환 메소드 스펙
	 */
	public static MethodSpec getAltMethod(String className, String methodName, String signature) {
		
		MethodPair methodPair = getMethodPair(className, methodName, signature);
		
		if(methodPair != null) {
			return methodPair.getAltMethod();
		} else {
			return null;
		}
	}
}
