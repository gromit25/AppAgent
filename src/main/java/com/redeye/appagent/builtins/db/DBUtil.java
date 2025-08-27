package com.redeye.appagent.builtins.db;

import java.sql.SQLException;

import com.redeye.appagent.logger.Log;

/**
 * 데이터베이스 Utility 클래스
 *
 * @author jmsohn
 */
public class DBUtil {
	
	/**
	 * 실행 시간 로깅
	 * 
	 * @param apiType API 타입
	 * @param obj 관련 객체
	 * @param message 메시지
	 * @param method 실행할 메소드
	 * @return 실행 후 결과
	 */
	public static <T> T writeExecTime(
		String apiType,
		Object obj,
		String message,
		DBMethod<T> method
	) throws SQLException {

		// 메소드 실행 및 수행 시간 측정
		long start = System.currentTimeMillis();
		T result = method.execute();
		long end = System.currentTimeMillis();

		// 로그 write
		if(obj != null) {
			Log.write(apiType, obj, end-start, message);
		} else {
			Log.write(apiType, result, end-start, message);
		}

		// 결과 반환
		return result;
	}
}
