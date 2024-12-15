package com.redeye.appagent.agent;

import java.lang.instrument.Instrumentation;

import com.redeye.appagent.config.Config;

/**
 * Babe Agent Main<br>
 * 설정값 로딩 수행 및 스레드 생성 및 동작
 *  
 * @author jmsohn
 */
public final class BabeAgent {
	
	/**
	 * Java VM의 Agent에 Transformer 등록
	 * 
	 * @param args 입력 Argument
	 * @param inst 현재 VM의 Instrument
	 */
	public static void premain(String args, Instrumentation inst) {
		
		try {
			
			//-----------------------
			// 환경 변수에서 설정값을 읽음
			Config.init();
			
			//-----------------------
			// Transformer 등록하여 변환클래스 등록
			BabeTransformer transformer = new BabeTransformer(
				Config.TRANSFORM_CONFIG_FILE.getValue()
			);
			
			inst.addTransformer(transformer, true);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
