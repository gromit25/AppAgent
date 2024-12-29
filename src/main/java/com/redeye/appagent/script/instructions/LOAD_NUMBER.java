package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * 스택에 숫자를 추가하는 명령어 클래스
 * 
 * @author jmsohn
 */
public class LOAD_NUMBER extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		double value = Double.parseDouble(this.getParam(0));
		stack.push(value);
	}

}
