package com.redeye.appagent.transform;

import lombok.Data;

/**
 * 메소드 변환 Map<br>
 * 대상 메소드 - 변환 메소드
 * 
 * @author jmsohn
 */
@Data
class AlterMethodMap {
	
	/** 대상 메소드 스펙 */
	private MethodSpec targetMethod;
	
	/** 변환 메소드 스펙 */
	private MethodSpec alterMethod;
}
