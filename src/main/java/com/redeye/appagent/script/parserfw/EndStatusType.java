package com.redeye.appagent.script.parserfw;

/**
 * 종료 상태의 종류
 * 
 * @author jmsohn
 */
public enum EndStatusType {
	
	/** 일반적인 종료 상태로 설정된 상태로 종료되면 정상 종료 */
	NORMAL_END,
	/**
	 * 설정된 상태로 변경되면 파싱을 종료함<br>
	 * - 변경시 TransferEventHandler는 실행 후 종료됨
	 */
	IMMEDIATELY_END,
	/** 설정된 상태로 변경되면 오류임 */
	ERROR
}
