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
    			methodSpec.append(getType2ByteCode(pType));
    		}
		}
		
		// 메소드 파라미터 종료
		methodSpec.append(")");
		
		// 리턴 타입 추가
		Class<?> rType = method.getReturnType();
		methodSpec.append(getType2ByteCode(rType));
		
		// 생성된 메소드 스펙 반환
		return methodSpec.toString();
	}
	
	/**
	 * 주어진 클래스를 바이트 코드 형식 문자열로 변환하여 반환
	 * 
	 * @param type 변환할 타입
	 * @return 바이트 코드 문자열
	 */
	private static String getType2ByteCode(Class<?> type) throws Exception {
		
		// 입력값 검증
		if(type == null) {
			throw new AgentException("type is null.");
		}
		
		// 타입 명 획득
		String orgTypeName = type.getTypeName();
		if(orgTypeName == null || orgTypeName.equals("") == true) {
			throw new AgentException("type name is null or blank.");
		}

    	// 바이트코드 타입명 변수
    	StringBuilder typeNameBuilder = new StringBuilder("");
    	
    	// 배열일 경우, 배열 차원의 수 변수
    	int dimensionCnt = 0;
	    	
    	// 파싱 상태
    	//   0: 타입명 상태
    	//   1: 배열 상태
    	int state = 0;
    	for(int index = 0; index < orgTypeName.length(); index++) {
    		
    		char ch = orgTypeName.charAt(index);
    		
    		if(state == 0) {
    			if(ch != '[') {
    				
    				// '.'일 경우 '/'로 변환
    				// ex) com.redeye.Test -> com/redeye/Test
    				if(ch == '.') {
    					typeNameBuilder.append('/');
    				} else {
    					typeNameBuilder.append(ch);
    				}
    				
    			} else {
    				
    				// ch가 '[' 일 경우 배열이 시작되므로,
    				// 상태를 배열상태(1)로 변경 및 차원수를 1 증가
    				state = 1;
    				dimensionCnt++;
    			}
    			
    		} else {
    			
    			// 배열 상태에서 '['이 들어올 경우 차원수를 1 증가
    			if(ch == '[') {
    				dimensionCnt++;
    			}
    		}
    	}
	    	
    	// 바이트코드 타입명 변수(typeName)과 배열차원수(dimensionCnt)를 이용하여
    	// 바이트코드 문자열을 생성함
    	StringBuffer bytecodeType = new StringBuffer("");
    	
    	// 배열 차원수 만큼 '['을 앞쪽에 채워줌
    	for(int index = 0; index < dimensionCnt; index++) {
    		bytecodeType.append("[");
    	}
    	
    	// primitive 타입이면 primitive 타입의 문자열 추가
    	// primitive 타입이 아니면 타입의 바이트 코드 문자열 추가
    	// -> L타입; ex) Lcom/redeye/Test;
    	String typeName = typeNameBuilder.toString();
    	
    	if(PrimitiveType.isPrimitive(typeName) == true) {
    		bytecodeType.append(PrimitiveType.get(typeName).getCode());
    	} else {
    		bytecodeType
    			.append("L")
    			.append(typeName)
    			.append(";");
    	}
    	
    	// 만들어진 바이트코드 타입 문자열을 반환
    	return bytecodeType.toString();
	}
}
