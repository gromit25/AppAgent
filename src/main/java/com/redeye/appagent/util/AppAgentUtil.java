package com.redeye.appagent.util;

import com.redeye.appagent.Config;

/**
 * AppAgent 전용 유틸리티 클래스
 * 
 * @author jmsohn
 */
public class AppAgentUtil {
	
	/**
	 * 시스템 명칭 반환
	 * 
	 * @return 시스템 환경 정보
	 */
	public static String getSysName() {
		return Config.SYSTEM_PID.getValue() + "@" + Config.SYSTEM_NAME.getValue();
	}
}
