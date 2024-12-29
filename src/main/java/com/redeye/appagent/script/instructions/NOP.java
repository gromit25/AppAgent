package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * No Operation 명령어 클래스
 * 
 * @author jmsohn
 */
public class NOP extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		// Do nothing
	}

}
