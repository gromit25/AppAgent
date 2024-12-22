package com.redeye.appagent.transform;

import org.objectweb.asm.Opcodes;

import com.redeye.appagent.exception.AgentException;
import com.redeye.appagent.util.PrimitiveType;

/**
 * transform 유틸리티 클래스
 * 
 * @author jmsohn
 */
class Util {
	
	/**
	 * 주어진 클래스를 바이트 코드 형식 문자열로 변환하여 반환
	 * 
	 * @param type 변환할 타입
	 * @return 바이트 코드 문자열
	 */
	public static String getType2ByteCode(Class<?> type) throws Exception {
		
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
    	int dimCnt = 0;
	    	
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
    				dimCnt++;
    			}
    			
    		} else {
    			
    			// 배열 상태에서 '['이 들어올 경우 차원수를 1 증가
    			if(ch == '[') {
    				dimCnt++;
    			}
    		}
    	}
	    	
    	// 바이트코드 타입명 변수(typeName)과 배열차원수(dimensionCnt)를 이용하여
    	// 바이트코드 문자열을 생성함
    	StringBuffer bytecodeType = new StringBuffer("");
    	
    	// 배열 차원수 만큼 '['을 앞쪽에 채워줌
    	for(int index = 0; index < dimCnt; index++) {
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
	
	/**
	 * 함수 호출 명령어 여부 반환
	 * 
	 * @param opcode 명령어 코드
	 * @return 함수 호출 명령어 여부
	 */
	static boolean isInvokeOp(int opcode) {
		
		if(opcode == Opcodes.INVOKESTATIC
			|| opcode == Opcodes.INVOKEVIRTUAL
			|| opcode == Opcodes.INVOKEINTERFACE
			|| opcode == Opcodes.INVOKESPECIAL ) {
			
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 함수 호출 코드에 대해 호출 종류를 문자열로 변환하여 반환
	 * 
	 * @param opcode 함수 호출 코드
	 * @return 변환된 문자열
	 */
	static String getInvokeName(int opcode) {
		
		String invokeName = "N/A(" + opcode + ")";
		
		switch(opcode) {
		
		case Opcodes.INVOKESTATIC:
			invokeName = "INVOKESTATIC";
			break;
			
		case Opcodes.INVOKEVIRTUAL:
			invokeName = "INVOKEVIRTUAL";
			break;
			
		case Opcodes.INVOKEINTERFACE:
			invokeName = "INVOKEINTERFACE";
			break;
			
		case Opcodes.INVOKESPECIAL:
			invokeName = "INVOKESPECIAL";
			break;
			
		case Opcodes.INVOKEDYNAMIC:
			invokeName = "INVOKEDYNAMIC";
			break;
		}
		
		return invokeName;
	}
}
