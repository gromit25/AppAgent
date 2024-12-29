package com.redeye.appagent.script.instructions;

import java.util.Map;
import java.util.Stack;

/**
 * 숫자 이항 연산 명령어 추상 클래스
 * 
 * @author jmsohn
 */
public abstract class BiNumInstruction extends Instruction {
	
	/**
	 * 이항 연산 수행
	 * 
	 * @param p1 첫번째 파라미터
	 * @param p2 두번째 파라미터
	 * @return 연산 결과
	 */
	public abstract Object process(Double p1, Double p2) throws Exception;

	@Override
	public void execute(Stack<Object> stack, Map<String, ?> values) throws Exception {
		
		// 파라미터를 스택에서 꺼내옴
		Object p2 = stack.pop();
		Object p1 = stack.pop();
		
		// p1, p2에 대한 검증 수행
		if(p2 == null) {
			throw new NullPointerException("p2 value is null");
		}
		
		if(p1 == null) {
			throw new NullPointerException("p1 value is null");
		}
		
		if((p2 instanceof Double) == false) {
			throw new Exception("Unexpected type:" + p2.getClass());
		}
		
		if((p1 instanceof Double) == false) {
			throw new Exception("Unexpected type:" + p1.getClass());
		}
		
		// p1, p2에 대한 연산 수행
		// 수행결과는 숫자(double) 또는 boolean 값
		Object result = this.process((Double)p1, (Double)p2);
		
		if((result instanceof Double) == false &&
			(result instanceof Boolean) == false) {
			throw new Exception("result value is not expected type:" + result.getClass());
		}
		
		// 스택에 결과 푸시
		stack.push(result);

	}

}
