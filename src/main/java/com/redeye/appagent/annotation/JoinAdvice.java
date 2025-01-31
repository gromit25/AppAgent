package com.redeye.appagent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 조인 어드바이스 어노테이션
 * 
 * @author jmsohn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JoinAdvice {
	
	/**
	 * 조인 어드바이스 문자열<br>
	 * ex) com/test/Test.test(Ljava/lang/String;), com/test/*
	 */
	String value();
}
