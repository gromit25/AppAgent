package com.redeye.appagent.builtins.db;

import com.redeye.StringUtil;

/**
 * 데이터베이스 쿼리 수행 정보 저장용<br>
 * 스레드 별로 저장함
 * 
 * @author jmsohn
 */
public class ContentsDB {
	
	/** sql문 */
	private static ThreadLocal<String> sql = new ThreadLocal<String>();
	
	/** 파라미터 */
	private static ThreadLocal<String> params = new ThreadLocal<String>();
	
	
	/**
	 * 설정된값 전체 삭제
	 */
	public static void removeAll() {
		sql.remove();
		params.remove();
	}
	
	/**
	 * 쿼리 반환
	 * 
	 * @return 쿼리
	 */
	public static String getSql() {
		return sql.get();
	}
	
	/**
	 * 쿼리 설정
	 * 
	 * @param sql 쿼리
	 */
	public static void setSql(String sql) {
		ContentsDB.sql.set(sql);
	}
	
	/**
	 * 파라미터 목록 반환
	 * 
	 * @return
	 */
	public static String getParams() {
		return (params.get() == null) ? "" : params.get();
	}

	/**
	 * 파라미터 추가
	 * 
	 * @param param 추가할 파라미터
	 */
	public static void addParam(String param) {
		
		String params = (StringUtil.isEmpty(getParams()) == true) ?	param : getParams() + "," + param;
		ContentsDB.params.set(params);
	}
}
