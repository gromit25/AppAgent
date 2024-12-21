package com.redeye.appagent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.redeye.appagent.logger.ApiType;

/**
 * 대상 클래스 어노테이션
 * 
 * @author jmsohn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TargetClass {
	
	/**
	 * 대상 클래스 타입
	 * 
	 * @return 대상 클래스 타입
	 */
	ApiType type();
	
	/**
	 * 대상 클래스 명<br>
	 * ex) java/sql/PreparedStatement
	 * 
	 * @return 대상 클래스 명
	 */
	String cls();
}
