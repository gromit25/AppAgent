package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * Not 연산 명령어 클래스
 * 
 * @author jmsohn
 */
public class NOT extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		Object p1 = stack.pop();
		
		if(p1 instanceof Boolean == false) {
			throw new Exception("Unexpected type: " + p1.getClass());
		}
		
		stack.push(!((Boolean)p1));
	}

}
