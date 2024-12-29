package com.redeye.appagent.script.instructions;

/**
 * 뺄셈 명령어 클래스
 * 
 * @author jmsohn
 */
public class MINUS extends BiNumInstruction {

	@Override
	public Object process(Double p1, Double p2) throws Exception {
		return p1 - p2;
	}

}
