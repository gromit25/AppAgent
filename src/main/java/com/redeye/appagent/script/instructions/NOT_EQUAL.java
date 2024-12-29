package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

public class NOT_EQUAL extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		Object p2 = stack.pop();
		Object p1 = stack.pop();
		
		if(p1 == null || p2 == null) {
			stack.push(p1 != p2);
		} else {
			stack.push(!p1.equals(p2));
		}
	}

}
