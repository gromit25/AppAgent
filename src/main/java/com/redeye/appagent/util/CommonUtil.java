package com.redeye.appagent.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.redeye.appagent.exception.AgentException;

/**
 * AppAgent 공통 유틸리티 클래스
 * 
 * @author jmsohn
 */
public class CommonUtil {

	/**
	 * 변환 메소드 스펙 반환
	 * 
	 * @param method 변환 메소드
	 * @return 변환 메소드 스펙
	 */
	public static String getAltMethodSpec(Method method) throws Exception {
		
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

		// 반환할 메소드 스펙 문자열 변수
		StringBuilder methodSpec = new StringBuilder("");
		
		// 메소드 명 추가
		methodSpec
			.append(method.getName())
			.append("(");
		
		// 메소드 파라미터 스펙 추가
		if(method.getParameterCount() > 0) {
			
    		for(Class<?> pType : method.getParameterTypes()) {
    			methodSpec.append(getClassByteCode(pType));
    		}
		}
		
		// 메소드 파라미터 종료
		methodSpec.append(")");
		
		// 리턴 타입 추가
		Class<?> rType = method.getReturnType();
		methodSpec.append(getClassByteCode(rType));
		
		// 생성된 메소드 스펙 반환
		return methodSpec.toString();
	}
	
	/**
	 * 주어진 클래스를 바이트 코드 형식 문자열로 변환하여 반환
	 * 
	 * @param clazz 변환할 클래스
	 * @return 바이트 코드 문자열
	 */
	private static String getClassByteCode(Class<?> clazz) throws Exception {
		
		// 입력값 검증
		if(clazz == null) {
			throw new AgentException("class is null.");
		}
		
		// 현재 클래스의 타입 명 획득
		String typeName = clazz.getCanonicalName();
		if(typeName == null || typeName.equals("") == true) {
			throw new AgentException("type name is null or blank.");
		}
		
		// 기본형일 경우 기본형 바이트 코드 반환
		// 만일 클래스일 경우 클래스의 바이트 코드 반환
		if(clazz.isPrimitive() == true) {
			
			PrimitiveType pType = PrimitiveType.get(typeName);
			if(pType == null) {
				throw new AgentException("invalid primitive type name:" + typeName);
			}
			
			return pType.getCode();
			
		} else {
			
			typeName = typeName.replace('.', '/');
			return "L" + typeName + ";";
		}
	}
}
