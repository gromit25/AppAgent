package com.redeye.appagent;

import java.lang.instrument.Instrumentation;

import com.redeye.appagent.transform.AppTransformer;
import com.redeye.appagent.transform.MethodMap;
import com.redeye.appagent.wrapper.db.DataSourceWrapper;

/**
 * App Agent Main<br>
 * 설정값 로딩 수행 및 스레드 생성 및 동작
 *  
 * @author jmsohn
 */
public final class AppAgent {
	
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
			
			// 메소드 변환맵 초기화
			MethodMap.init(
				DataSourceWrapper.class
			);
			
			//-----------------------
			// Transformer 등록하여 변환클래스 등록
			AppTransformer transformer = new AppTransformer();
			
			inst.addTransformer(transformer, true);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
