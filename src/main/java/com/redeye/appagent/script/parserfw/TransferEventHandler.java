package com.redeye.appagent.script.parserfw;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 전이함수를 통해 전이가 일어날 경우(Transfer Event)<br>
 * 호출되는 메소드를 표시하는 Annotation
 * 
 * @author jmsohn
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface TransferEventHandler {
	
	/** 전이 이전 상태 */
	String[] source();
	/** 전이 이후 상태 */
	String[] target();
	
}
