package com.redeye.babe.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;

import com.redeye.babe.config.RootConfig;

/**
 * Babe Agent Main
 * 설정값 로딩 수행 및 스레드 생성 및 동작 
 * @author jmsohn
 */
public final class BabeAgent {
	
	/**
	 * Java VM의 Agent에 Transformer 등록
	 * @param args 입력 Argument
	 * @param inst 현재 VM의 Instrument
	 */
	public static void premain(String args, Instrumentation inst) {
		
		try {
			
			//-----------------------
			// 설정값이 정상적으로 로딩되었는지 확인
			// RootConfig는 class 로딩시, 설정을 읽어 들인다.
			// RootConfig class내의 static {} 참조
			if(RootConfig.isValid() == false) {
				System.exit(1);
			}
			
			//-----------------------
			// Transformer 등록하여 변환클래스 등록
			BabeTransformer transformer = new BabeTransformer(
					RootConfig.TRANSFORM_FILE.getValueObject(File.class)); 
			inst.addTransformer(transformer, true);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
