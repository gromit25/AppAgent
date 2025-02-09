package com.redeye.appagent.rewriter;

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
	
	/** 조인 어드바이스 목록 */
	private List<JoinAdvice> advices;
	
	/** 대상 메소드 스펙 */
	@Getter
	private MethodSpec targetMethod;
	
	/** 변환 메소드 스펙 */
	@Getter
	private MethodSpec altMethod;
	
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
		
		// Method Pair 목록 변수
		List<MethodPair> methodPairList = new ArrayList<>();
		
		// 변환 대상 클래스 획득
		TargetClass targetClass = cls.getDeclaredAnnotation(TargetClass.class);
		if(targetClass == null) {
			return methodPairList;
		}
		
		// 각 메소드 별 변환 대상 메소드 목록 획득
		Method[] methods = cls.getDeclaredMethods();
		for(Method method: methods) {
			
			// 메소드의 조인 어드바이스 객체 생성
			List<JoinAdvice> advices = JoinAdvice.create(method);
			
			// TargetMethod 어노테이션이 있는 경우에만 처리
			TargetMethod targetMethod = method.getDeclaredAnnotation(TargetMethod.class);
			if(targetMethod == null) {
				continue;
			}
			
			// 변환 메소드 짝 생성
			MethodPair methodPair = new MethodPair();
			
			// 조인 어드바이스 목록 설정
			methodPair.advices = advices;
			
			// 변환 대상 메소드 정보 생성 및 설정
			methodPair.targetMethod = MethodSpec.create(
				targetClass.cls(),
				targetMethod.value()
			);
			
			// 변환 메소드 정보 생성 및 설정
			methodPair.altMethod = MethodSpec.create(method);
			
			// 메소드 짝 목록에 추가
			methodPairList.add(methodPair);
		}
		
		// Method Pair 목록 변수 반환
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
		this.altMethod = MethodSpec.create(method);
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
	
	/**
	 * 주어진 클래스 명 및 메소드 명에 대해 조인할 것인지 여부 반환
	 * 
	 * @param className 검사할 클래스 명
	 * @param methodName 검사할 메소드 명
	 * @return 조인 여부
	 */
	boolean isJoin(String className, String methodName) {
		
		// 설정된 어드바이스가 없을 경우 false 반환
		if(this.advices == null || this.advices.size() == 0) {
			return false;
		}

		// 설정된 어드바이스 중 하나라도 일치하면 true 반환
		for(JoinAdvice advice: this.advices) {
			if(advice.isJoin(className, methodName) == true) {
				return true;
			}
		}
		
		// 설정된 어드바이스 중 하나도 일치하지 않는 경우 false 반환
		return false;
	}
	
	@Override
	public String toString() {
		
		StringBuilder toString = new StringBuilder("");
		
		toString
			.append(this.targetMethod)
			.append(" -> ")
			.append(this.altMethod);
		
		return toString.toString();
	}
}
