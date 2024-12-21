package com.redeye.appagent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메소드 호출 변환 어노테이션
 * 
 * @author jmsohn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TargetMethod {

	/**
	 * 변환 대상 메소드명과 시그니처 문자열 반환<br>
	 * ex) executeQuery()Ljava/sql/ResultSet;
	 * 
	 * @return 변환 대상 메소드명과 시그니처 문자열
	 */
	String value();
}
