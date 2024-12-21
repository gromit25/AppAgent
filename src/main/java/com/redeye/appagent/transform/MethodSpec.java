package com.redeye.appagent.transform;

import java.lang.reflect.Method;

import com.redeye.appagent.exception.AgentException;
import com.redeye.appagent.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

/**
 * 메소드 스펙
 * 
 * @author jmsohn
 */
class MethodSpec {
	
	/** 클래스 명(ex. com.redeye.appagent.AppAgent) */
	@Getter
	@Setter
	private String className;
	
	/** 메소드 명 */
	@Getter
	@Setter
	private String methodName;
	
	/** 메소드 시그니처 */
	@Getter
	@Setter
	private String signature;
	
	/**
	 * 생성자<br>
	 * create 메소드를 통해서만 생성 가능하도록 private 으로 선언
	 */
	private MethodSpec() {
	}
	
	/**
	 * 메소드 스펙 생성 및 반환
	 * 
	 * @param className 클래스 명
	 * @param methodName 메소드 명
	 * @param signature 메소드 시그니처
	 * @return 생성된 메소드 스펙
	 */
	static MethodSpec create(String className, String methodName, String signature) throws Exception {
		
		// 입력값 검증
		if(StringUtil.isBlank(className) == true) {
			throw new AgentException("class name is null or blank.");
		}
		
		if(StringUtil.isBlank(methodName) == true) {
			throw new AgentException("method name is null or blank.");
		}
		
		if(StringUtil.isBlank(signature) == true) {
			throw new AgentException("method signature is null or blank.");
		}
		
		// 메소드 스펙 객체 생성
		MethodSpec spec = new MethodSpec();
		
		// 메소드 스펙값 설정
		spec.setClassName(className);
		spec.setMethodName(methodName);
		spec.setSignature(signature);
		
		return spec;
	}
	
	/**
	 * 메소드 스펙 생성 및 반환
	 * 
	 * @param method 변환 메소드
	 * @return 생성된 메소드 스펙
	 */
	static MethodSpec create(Method method) throws Exception {
		
		// 입력값 검증
		if(method == null) {
			throw new AgentException("method is null.");
		}
		
		// 반환할 메소드 스펙 객체 생성
		MethodSpec methodSpec = new MethodSpec();
		
		// ---- 클래스 명 설정
		methodSpec.setClassName(method.getDeclaringClass().getCanonicalName());
		
		// ---- 메소드 명 설정
		methodSpec.setMethodName(method.getName());
		
		// ---- 메소드 시그니쳐 생성 및 설정
		StringBuilder signBuilder = new StringBuilder("(");
		
		// 메소드 파라미터 스펙 추가
		if(method.getParameterCount() > 0) {
			
    		for(Class<?> pType : method.getParameterTypes()) {
    			signBuilder.append(Util.getType2ByteCode(pType));
    		}
		}
		
		// 메소드 파라미터 종료
		signBuilder.append(")");
		
		// 리턴 타입 추가
		Class<?> rType = method.getReturnType();
		signBuilder.append(Util.getType2ByteCode(rType));
		
		// ---- 생성된 메소드 스펙 반환
		return methodSpec;
	}
	
	@Override
	public boolean equals(Object other) {
		
		// 비교 객체가 MethodSpec 타입이 아닐 경우 false 반환
		if(other instanceof MethodSpec) {
			return false;
		}
		
		// 현재 객체의 값이 모두 설정 되어 있지 않으면 false 반환
		if(this.className == null || this.methodName == null || this.signature == null) {
			return false;
		}
		
		// 비교 객체와 같이 일치하는지 여부 확인
		MethodSpec otherSpec = (MethodSpec)other;
		
		if(this.className.equals(otherSpec.getClassName()) == false) {
			return false;
		}
		
		if(this.methodName.equals(otherSpec.getMethodName()) == false) {
			return false;
		}
		
		if(this.signature.equals(otherSpec.getSignature()) == false) {
			return false;
		}
		
		// 모든 테스트가 통과하면 true 를 반환
		return true;
	}
	
	@Override
	public String toString() {
		
		StringBuilder toString = new StringBuilder("");
		
		toString
			.append(this.className)
			.append(".")
			.append(this.methodName)
			.append(this.signature);
		
		return toString.toString();
	}
}
