package com.redeye.appagent.wrapper.db;

/**
 * 데이터베이스 쿼리 수행 정보
 * @author jmsohn
 */
public class ContentsDB {
	
	/** sql문의 해시값(SHA-256) */
	private static ThreadLocal<String> hash = new ThreadLocal<String>();
	/** sql문 */
	private static ThreadLocal<String> sql = new ThreadLocal<String>();
	/** 파라미터 */
	private static ThreadLocal<String> params = new ThreadLocal<String>();
	
	/**
	 * 설정된값 전체 삭제
	 */
	public static void removeAll() {
		hash.remove();
		sql.remove();
		params.remove();
	}
	
	public static String getSql() {
		return sql.get();
	}
	
	public static void setSql(String sql) {
		ContentsDB.sql.set(sql);
	}
	
	public static String getParams() {
		if(params.get() == null) {
			return "";
		} else {
			return params.get();
		}
	}
	
	public static void setParams(String params) {
		ContentsDB.params.set(params);
	}

	public static String getHash() {
		return hash.get();
	}

	public static void setHash(String hash) {
		ContentsDB.hash.set(hash);
	}
}
