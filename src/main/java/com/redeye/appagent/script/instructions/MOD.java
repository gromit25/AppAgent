package com.redeye.appagent.script.instructions;

/**
 * Modular 연산 
 * 
 * @author jmsohn
 */
public class MOD extends BiNumInstruction {

	@Override
	public Object process(Double p1, Double p2) throws Exception {
		
		if(p2 == 0) {
			return (double)0.0;
		}
		
		return p1 % p2;
	}

}
