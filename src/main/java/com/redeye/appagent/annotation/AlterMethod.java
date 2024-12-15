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
public @interface AlterMethod {

	/**
	 * 변환 대상 메소드 시그니처 문자열 반환
	 * 
	 * @return 변환 대상 메소드 시그니처 문자열
	 */
	String value();
}
