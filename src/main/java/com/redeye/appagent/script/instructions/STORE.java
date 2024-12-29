package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * values에 스택의 값 저장하는 명령어 클래스<br>
 * 스택에서 값을 꺼내어 파라미터에 설정된 이름으로 values 저장함<br>
 * <pre>
 * ex) LOAD 1
 *     LOAD 2
 *     STORE value1, value2
 *     
 *     -> values 내에 value1: 1, value2: 2 가 저장됨 
 * </pre>
 * 
 * @author jmsohn
 */
public class STORE extends Instruction {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		// 설정된 모든 파라미터에 대해
		// 스택의 값을 values에 모두 저장
		for(int index = 0; index < this.getParamCount(); index++) {
			
			// 스택에서 값을 가져옴
			Object p1 = stack.pop();
			if(p1 == null) {
				throw new NullPointerException("stack is empty");
			}
			
			// 스택의 값을 values에 넣음
			// 스택의 경우 처음 입력된 것이 나중에 나오게 되어
			// 뒤에 설정된 파라미터 부터 values에 넣음
			String name = this.getParam(this.getParamCount() - 1 - index);
			((Map<String, Object>)values).put(name, p1);
		}
	}

}
