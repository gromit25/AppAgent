package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * 덧셈 명령어 클래스
 * 
 * @author jmsohn
 */
public class ADD extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		// 파라미터를 스택에서 꺼내옴
		Object p2 = stack.pop();
		Object p1 = stack.pop();
		
		// p1, p2에 대한 검증 수행
		if(p2 == null) {
			throw new NullPointerException("p2 value is null");
		}
		
		if(p1 == null) {
			throw new NullPointerException("p1 value is null");
		}
		
		// 결과 변수
		Object result = null; 
		
		// p1, p2가 모두 Double 형일 경우, 덧샘 수행
		// 아닐 경우 문자열 더하기 연산 수행
		if((p1 instanceof Double) == true && (p2 instanceof Double) == true) {
			
			// p1, p2에 대한 덧셈 연산 수행
			result = ((Double)p1) + ((Double)p2);

		} else {
			
			// p1, p2의 문자열을 합침
			result = toString(p1) + toString(p2);
		
		}
		
		// 스택에 결과 푸시
		stack.push(result);
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	private static String toString(Object p) throws Exception {
		
		if(p instanceof Double) {
			
			String pStr = p.toString();
			if(pStr.endsWith(".0") == true) {
				pStr = pStr.substring(0, pStr.length() - 2);
			}
			
			return pStr;
				
		} else {
			return p.toString();
		}
	}
}
