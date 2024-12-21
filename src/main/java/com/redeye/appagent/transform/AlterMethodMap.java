package com.redeye.appagent.transform;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.redeye.appagent.exception.AgentException;

import lombok.Getter;

/**
 * 메소드 변환 Map<br>
 * 대상 메소드 - 변환 메소드
 * 
 * @author jmsohn
 */
class AlterMethodMap {
	
	/** 대상 메소드 스펙 */
	@Getter
	private MethodSpec targetMethod;
	
	/** 변환 메소드 스펙 */
	@Getter
	private MethodSpec alterMethod;
	
	/**
	 * 대상 메소드 스펙 설정
	 * 
	 * @param className 클래스 명
	 * @param methodName 메소드 명
	 * @param signature 메소드 시그니처
	 */
	void setTargetMethod(String className, String methodName, String signature) throws Exception {
		this.targetMethod = MethodSpec.create(className, methodName, signature);
	}
	
	/**
	 * 변환 메소드 설정
	 * 
	 * @param method 변환 메소드 객체
	 */
	void setAlterMethod(Method method) throws Exception {
		
		// 입력값 검증
		if(method == null) {
			throw new AgentException("method is null.");
		}
		
		// 변환 메소드가 static 메소드가 아니면 예외 발생
		// 변환 메소드는 항상 static 이어야 함
		if(Modifier.isStatic(method.getModifiers()) == false) {
			throw new AgentException(method.getDeclaringClass().getCanonicalName() + "."
					+ method.getName() + " is not static method.");
		}
		
		// 변환 메소드 스펙 생성 및 설정
		this.alterMethod = MethodSpec.create(method);
	}
	
	@Override
	public String toString() {
		
		StringBuilder toString = new StringBuilder("");
		
		toString
			.append(this.targetMethod)
			.append(" -> ")
			.append(this.alterMethod);
		
		return toString.toString();
	}
}
