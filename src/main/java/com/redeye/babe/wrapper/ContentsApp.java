package com.redeye.babe.wrapper;

/**
 * Application 수행 정보(기본적으로 모든 형태의 Application에 들어감) 
 * ThreadLocal을 사용하여 Thread 내에서 공유할 수 있도록 함
 * @author jmsohn
 */
public class ContentsApp {
	
	/** transaction id */
	private static ThreadLocal<String> txId = new ThreadLocal<String>();
	/**
	 * 시작 시간 저자용
	 * 생성자등 일부 경우에, start 시간을 저장해서
	 * 전달할 경우가 발생함
	 */
	private static ThreadLocal<Long> start = new ThreadLocal<Long>();
	
	/**
	 * transaction id를 가져옴
	 * @return transaction id
	 */
	public static String getTxId() {
		return txId.get();
	}

	/**
	 * transaction id를 설정함
	 * @param txId transaction id
	 */
	public static void setTxId(String txId) {
		ContentsApp.txId.set(txId);
	}

	/**
	 * 시작시간
	 * @return 시작시간
	 */
	public static long getStart() {
		return start.get();
	}

	/**
	 * 시작시간 설정
	 * @param start 시작시간
	 */
	public static void setStart(long start) {
		ContentsApp.start.set(start);
	}

}
