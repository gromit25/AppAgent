package com.redeye.appagent.transform;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.exception.AgentException;

import lombok.Getter;

/**
 * 메소드 변환 짝<br>
 * 대상 메소드 - 변환 메소드
 * 
 * @author jmsohn
 */
class MethodPair {
	
	/** 대상 메소드 스펙 */
	@Getter
	private MethodSpec targetMethod;
	
	/** 변환 메소드 스펙 */
	@Getter
	private MethodSpec alterMethod;
	
	/**
	 * 클래스의 TargetClass, TargetMethod를 이용,<br>
	 * MethodPair 객체 생성 및 반환
	 * 
	 * @param cls 검사할 클래스
	 * @return 생성된 MethodPair 목록
	 */
	static List<MethodPair> load(Class<?> cls) throws Exception {
		
		// 입력 값 검증
		if(cls == null) {
			throw new AgentException("class is null.");
		}
		
		//
		List<MethodPair> methodPairList = new ArrayList<>();
		
		//
		TargetClass targetClass = cls.getDeclaredAnnotation(TargetClass.class);
		if(targetClass == null) {
			return methodPairList;
		}
		
		//
		Method[] methods = cls.getDeclaredMethods();
		for(Method method: methods) {
			
			TargetMethod targetMethod = method.getDeclaredAnnotation(TargetMethod.class);
			if(targetMethod == null) {
				continue;
			}
			
			MethodPair methodPair = new MethodPair();
			
			methodPair.targetMethod = MethodSpec.create(
				targetClass.cls(),
				targetMethod.value()
			);
			methodPair.alterMethod = MethodSpec.create(method);
			
			methodPairList.add(methodPair);
		}
		
		return methodPairList;
	}
	
	/**
	 * 대상 메소드 스펙 설정
	 * 
	 * @param className 클래스 명
	 * @param methodSignature 메소드 시그니처
	 */
	void setTargetMethod(String className, String methodSignature) throws Exception {
		this.targetMethod = MethodSpec.create(className, methodSignature);
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
	
	/**
	 * Method Pair 키 반환<br>
	 * Target Method 문자열(ex. javax/sql/DataSource.getConnection(Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;)
	 * 
	 * @return Method Pair 키
	 */
	String getKey() {
		return this.getTargetMethod().toString();
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
