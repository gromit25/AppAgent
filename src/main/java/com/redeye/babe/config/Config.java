package com.redeye.babe.config;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

import lombok.Getter;

/**
 * 설정 값 관리 클래스<br>
 * 환경 변수에서 읽어와 설정함
 * 
 * @author jmsohn
 */
public enum Config {
	
	/** agent 의 패키지명 */
	AGENT_PACKAGE("AGENT_PACKAGE", "com/redeye/babe"),

	/** system 구분자(id) */
	SYSTEM_ID("SYSTEM_ID", "N/A"),

	/** system server name */
	SYSTEM_NAME("SYSTEM_NAME", "N/A"),
	
	/** system process id */
	SYSTEM_PID("SYSTEM_PID", "N/A"),
	
	/** CHARACTER SET */
	SYSTEM_CHARSET("SYSTEM_CHARSET", Charset.defaultCharset().name()),
	
	/** 변환 맵(TransformMap) 설정 파일명 */
	TRANSFORM_CONFIG_FILE("TRANSFORM_CONFIF_FILE", "N/A"),
	
	/** 로그 타입 */
	LOG_TYPE("LOG_TYPE", "FILE"),
	
	/** 스택 트레이스 정보를 남길 package 목록 */
	LOG_TRACE_PACKAGES("LOG_TRACE_PACKAGES", ""),
	
	/** 패키지 명 줄임 여부 */
	LOG_SHORT_PACKAGE_YN("LOG_SHORT_PACKAGE", "N"),
	
	/** 로그 파일 명 */
	LOG_FILE_PATH("LOG_FILE_PATH", "./agent.log"),
	
	/**
	 * 로그 파일의 최대치<br>
	 * 최대치가 넘을 경우 현재 로그 파일은 백업함
	 */
	LOG_FILE_MAXSIZE("LOG_MAX_SIZE", "1073741824"); // default 1GiB
	
	//---------------------------
	
	/**
	 * 설정 초기화 수행
	 */
	public static void init() throws Exception {
		
		// PID와 현재 서버명 설정
		// runtimeName = PID@현재서버명 형태로 되어 있음
		String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
		
		if(runtimeName != null) {
			
			String[] splitedRuntimeName = runtimeName.split("@");
			
			if(splitedRuntimeName.length > 1) {
				
				Config.SYSTEM_NAME.value = splitedRuntimeName[1];
				Config.SYSTEM_PID.value = splitedRuntimeName[0];
			}
		}
		
		// 환경 변수에서 설정 값을 읽어옴
		Config[] configs = Config.values();
		
		for(Config config: configs) {
			
			String value = System.getenv(config.key);
			
			// 설정된 값이 있을 경우에만 설정
			if(value != null) {
				config.value = value;
			}
		}
	}
	
	//---------------------------
	
	/** 환경 변수 키 */
	protected String key;
	
	/** 환경 변수 값 */
	@Getter
	protected String value;
	
	/**
	 * 설정 생성자
	 * 
	 * @param key 환경 변수 키
	 * @param value 환경 변수 디폴트 값
	 */
	private Config(String key, String defaultValue) {
		this.key = key;
		this.value = defaultValue;
	}
}
