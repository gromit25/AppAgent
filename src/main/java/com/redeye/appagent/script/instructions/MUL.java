package com.redeye.appagent.script.instructions;

/**
 * 곱셈 명령어 클래스
 * 
 * @author jmsohn
 */
public class MUL extends BiNumInstruction {

	@Override
	public Object process(Double p1, Double p2) throws Exception {
		return p1 * p2;
	}

}
