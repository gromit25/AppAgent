package com.redeye.appagent.script.instructions;

/**
 * 나눗셈 명령어 클래스
 * 
 * @author jmsohn
 */
public class DIV extends BiNumInstruction {

	@Override
	public Object process(Double p1, Double p2) throws Exception {
		
		if(p2 == 0) {
			if(p1 >= 0) {
				return Double.POSITIVE_INFINITY;
			} else {
				return Double.NEGATIVE_INFINITY;
			}
		}
		
		return p1 / p2;
	}

}
