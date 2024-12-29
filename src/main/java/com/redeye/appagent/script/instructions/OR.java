package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * or 명령어 클래스
 * 
 * @author jmsohn
 */
public class OR extends Instruction {

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
		
		if((p2 instanceof Boolean) == false) {
			throw new Exception("Unexpected type:" + p2.getClass());
		}
		
		if((p1 instanceof Boolean) == false) {
			throw new Exception("Unexpected type:" + p1.getClass());
		}
		
		// p1, p2에 대한 or 연산 수행
		Boolean result = (Boolean)p1 || (Boolean)p2;
		
		// 스택에 결과 푸시
		stack.push(result);
	}
}
