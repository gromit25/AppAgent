package com.redeye.appagent.script.instructions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method Alias를 설정하는 Annotation<br>
 * 설정하는 메소드는 public static 이어야 하며,<br>
 * return type은 double/Double 이어야 함
 * 
 * @author jmsohn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodAlias {
	String alias();
}
