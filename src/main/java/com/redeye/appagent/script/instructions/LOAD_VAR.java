package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * 스택에 변수를 추가하는 명령어 클래스
 * 
 * @author jmsohn
 */
public class LOAD_VAR extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		Object value = values.get(this.getParam(0));
		
		if(value == null) {
			stack.push(null);
		} else {
			Class<?> valueClass = value.getClass();
			
			if(valueClass == int.class || valueClass == Integer.class
				|| valueClass == long.class || valueClass == Long.class
				|| valueClass == float.class || valueClass == Float.class) {
				
				stack.push(((Number)value).doubleValue());
				
			} else {
				stack.push(value);
			}
		}
		
	}

}
