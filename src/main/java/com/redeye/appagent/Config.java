package com.redeye.appagent;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;

import lombok.Getter;

/**
 * AppAgent 설정 값<br>
 * 환경 변수에서 읽어와 설정함
 * 
 * @author jmsohn
 */
public enum Config {
	
	/** agent 의 패키지명 */
	AGENT_PACKAGE("AGENT_PACKAGE", "com/redeye/appagent"),

	/** system 구분자(id) */
	SYSTEM_ID("AGENT_SYSTEM_ID", "N/A"),

	/** system server name */
	SYSTEM_NAME("AGENT_SYSTEM_NAME", "N/A"),
	
	/** system process id */
	SYSTEM_PID("AGENT_SYSTEM_PID", "N/A"),
	
	/** CHARACTER SET */
	SYSTEM_CHARSET("AGENT_SYSTEM_CHARSET", Charset.defaultCharset().name()),
	
	//--- 조인(어느 위치에 메소드를 변경할 것인지) 방식
	
	/**
	 * 조인할 패키지 명(, 로 나눔)
	 * ex) com/redeye, com/prj
	 */
	JOIN_PACKAGE("AGENT_JOIN_PACKAGE", "com/test"),
	
	//--- 로그 관련
	
	/** 로그 타입 */
	LOG_TYPE("AGENT_LOG_TYPE", "FILE"),
	
	/** 출력 로그 포맷 */
	LOG_FORMAT("AGENT_LOG_FORMAT", ""),
	
	/** 로그 Writer(Logger 1개당 로그 Writer 1개임) 의 개수 */
	LOG_WRITER_COUNT("AGENT_LOG_WRITER_COUNT", "5"),
	
	/** 로그 큐의 최대 개수 - 주의) 최대 개수 이상이 큐에 있을 경우 로그가 쌓이지 않음 */
	LOG_MAX_QUEUE_COUNT("AGENT_LOG_MAX_QUEUE_COUNT", "1000"),
	
	/** 스택 트레이스 정보를 남길 package 목록 */
	LOG_TRACE_PACKAGES("AGENT_LOG_TRACE_PACKAGES", ""),
	
	/** 패키지 명 줄임 여부 */
	LOG_SHORT_PACKAGE_YN("AGENT_LOG_SHORT_PACKAGE", "N"),
	
	//--- 로그 타입: 파일 관련 설정
	
	/** 로그 파일 명 */
	LOG_FILE_PATH("AGENT_LOG_FILE_PATH", "./agent.log"),
	
	/** 로그 파일 관리자의 수행 주기(단위: 초) */
	LOG_FILE_MGR_PERIOD("AGENT_LOG_FILE_MGR_PERIOD", "10"),
	
	/**
	 * 로그 파일의 최대치(단위: MiB)<br>
	 * 최대치가 넘을 경우 현재 로그 파일은 백업함
	 */
	LOG_FILE_MGR_MAXSIZE("AGENT_LOG_FILE_MGR_MAX_SIZE", "1024"), // default 1GiB
	
	/** 유지할 백업 로그 파일의 개수 */
	LOG_FILE_MGR_BACKUP_COUNT("AGENT_LOG_FILE_MGR_BACKUP_COUNT", "2");
	
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
