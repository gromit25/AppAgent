package com.babe.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.HashSet;

import com.babe.agent.integrity.IntegrityChecker;
import com.babe.collectionwatcher.CollectionWatcher;
import com.babe.config.RootConfig;

/**
 * Babe Agent Main
 * 설정값 로딩 수행 및 스레드 생성 및 동작 
 * @author jmsohn
 */
public final class BabeAgent {
	
	private static CollectionWatcher collectionWatcher;
	
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
			
			// -----------------------
			// heap 확인을 위한 watcher 기동 수행
			BabeAgent.collectionWatcher = new CollectionWatcher(args, inst);
			BabeAgent.collectionWatcher.setDaemon(true);
			BabeAgent.collectionWatcher.start();
			
			//-----------------------
			// 주요 라이브러리 파일 무결성 체크
			HashSet<String> exceptionalCodeSources = IntegrityChecker.checkIntigrity(
					RootConfig.INTIGRITY_CHECK_FILE.getValueObject(File.class));
			
			//-----------------------
			// Transformer 등록하여 변환클래스 등록
			BabeTransformer transformer = new BabeTransformer(
					RootConfig.TRANSFORM_FILE.getValueObject(File.class)
					, exceptionalCodeSources); 
			inst.addTransformer(transformer, true);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	public static CollectionWatcher getCollectionWatcher() {
		return BabeAgent.collectionWatcher;
	}
}
