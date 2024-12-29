package com.redeye.appagent.script.instructions;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Stack;

import com.redeye.appagent.util.TypeUtil;

/**
 * 객체의 속성을 가져와서 스택에 넣는 명령어 클래스<br>
 * ex) message 객체에서 subject를 가져온다면<br>
 * message.subject -> message.getSubject()를 실행한 결과를 스택에 넣음
 * 
 * @author jmsohn
 */
public class LOAD_ATTR extends Instruction {

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		// 속성을 가져올 객체를 스택에서 가져옴
		Object obj = stack.pop();
		if(obj == null) {
			throw new NullPointerException("stack have no object.");
		}
		
		// 속성 메소드 수행하여 속성값을 반환 받음
		Method getMethod = TypeUtil.getGetter(obj.getClass(), this.getParam(0));
		Object attValue = getMethod.invoke(obj);
		
		// 속성값을 스택에 푸시
		stack.push(attValue);
	}
}
