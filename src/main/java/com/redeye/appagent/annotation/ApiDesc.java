package com.redeye.appagent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 설명(Description)<br>
 * API 관련 정보를 DB에 저장하지 위함
 * 
 * @author jmsohn
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ApiDesc {
	
	/**
	 * API 명(rc_api.api_nm) 반환
	 * 
	 * @return API 명
	 */
	String value();
	
	/**
	 * API 설명(rc_api.api_desc) 반환
	 * 
	 * @return API 설명
	 */
	String desc() default "";
	
	/**
	 * 인증 필요 여부(rc_api.cert_yn) 반환
	 * 
	 * @return 인증 필요 여부
	 */
	boolean certYn() default false;
	
	/**
	 * 요청자 명(rc_api.req_usr_nm) 반환
	 * 
	 * @return 요청자 명
	 */
	String requestor() default "N/A";
	
	/**
	 * 개발자 이메일(rc_usr.usr_email) 
	 * 
	 * @return 개발자 이메일
	 */
	String developer() default "";
}
