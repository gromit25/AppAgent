package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * True 값을 스택에 넣는 명령어 클래스
 * 
 * @author jmsohn
 */
public class LOAD_TRUE extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		stack.push(Boolean.TRUE);
	}
}
