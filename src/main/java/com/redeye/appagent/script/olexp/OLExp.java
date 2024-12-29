package com.redeye.appagent.script.olexp;

import java.util.HashMap;

import com.redeye.appagent.script.instructions.AbstractExp;
import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.olexp.parser.StoreParser;

/**
 * One Line Expression 클래스
 * 
 * @author jmsohn
 */
public class OLExp extends AbstractExp {
	
	/**
	 * 생성자
	 */
	protected OLExp(String exp) throws Exception {
		super(exp);
	}

	@Override
	protected AbstractParser<Instruction> getRootParser() throws Exception {
		return new StoreParser();
	}
	
	/**
	 * 생성 메소드
	 * 
	 * @param exp 명령어 문자열
	 * @return 생성된 명령어 처리 클래스
	 */
	public static OLExp compile(String exp) throws Exception {
		return new OLExp(exp);
	}
	
	/**
	 * 명령어 수행
	 * 
	 * @return 현재 객체(fluent 코딩용)
	 */
	public OLExp execute() throws Exception {
		this.execute(new HashMap<String, Object>());
		return this;
	}
}
