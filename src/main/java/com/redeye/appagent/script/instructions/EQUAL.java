package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * 두개의 객체가 동일한지 여부 확인 명령어 클래스
 * 
 * @author jmsohn
 */
public class EQUAL extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		Object p1 = stack.pop();
		Object p2 = stack.pop();
		
		if(p1 == null || p2 == null) {
			stack.push(p1 == p2);
		} else {
			stack.push(p1.equals(p2));
		}
	}

}
