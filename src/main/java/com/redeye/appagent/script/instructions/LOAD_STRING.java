package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * 스택에 문자열을 추가하는 명령어 클래스
 * 
 * @author jmsohn
 */
public class LOAD_STRING extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		stack.push(this.getParam(0).toString());
	}

}
