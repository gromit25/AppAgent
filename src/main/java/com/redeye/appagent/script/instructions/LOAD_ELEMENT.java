package com.redeye.appagent.script.instructions;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 
 * 
 * @author jmsohn
 */
public class LOAD_ELEMENT extends Instruction {

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		// 객체의 인덱스를 스택에서 가져옴
		Object index = stack.pop();
		if(index == null) {
			throw new NullPointerException("stack have no object.");
		}
		
		// 속성을 가져올 객체를 스택에서 가져옴
		Object obj = stack.pop();
		if(obj == null) {
			throw new NullPointerException("stack have no object.");
		}
		
		// 콜렉션 객체의 element 변수
		Object element = null;
		
		// List, Map 타입 별로 element를 가져옴
		if(List.class.isAssignableFrom(obj.getClass()) == true) {
			element = ((List)obj).get(toInt(index));
		} else if(Map.class.isAssignableFrom(obj.getClass()) == true) {
			element = ((Map)obj).get(index);
		} else {
			throw new Exception("unexpected type:" + obj.getClass().getCanonicalName());
		}
		
		// 속성값을 스택에 푸시
		stack.push(element);
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	private static int toInt(Object obj) throws Exception {
		
		if(
			obj.getClass() == int.class || obj instanceof Integer
		) {
			return (int)obj;
		} else if(obj.getClass() == long.class || obj instanceof Long) {
			return ((Long)obj).intValue();
		} else if(obj.getClass() == float.class || obj instanceof Float) {
			return ((Float)obj).intValue();
		} else if(obj.getClass() == double.class || obj instanceof Double) {
			return ((Double)obj).intValue();
		} else {
			throw new Exception("unexpected type: " + obj.getClass());
		}
	}
}
