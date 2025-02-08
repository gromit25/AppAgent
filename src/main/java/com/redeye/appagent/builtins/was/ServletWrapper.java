package com.redeye.appagent.builtins.was;

import java.io.IOException;

import com.redeye.appagent.annotation.JoinAdvice;
import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * 
 * @author jmsohn
 */
@TargetClass(cls="jakarta/servlet/Servlet", type="WAS")
public class ServletWrapper {
	
	/**
	 * 서블릿 실행 메소드
	 * 
	 * @param servlet 서블릿 객체
	 * @param req 사용자 요청 객체
	 * @param res 서버 응답 객체
	 */
	@TargetMethod("service(Ljakarta/servlet/ServletRequest;Ljakarta/servlet/ServletResponse;)V")
	@JoinAdvice("org/apache/catalina/core*")
	public static void service(
		Servlet servlet,
		ServletRequest req,
		ServletResponse res
	) throws ServletException, IOException {
		
		Log.write("WAS", servlet, 0, makeLogMsg(req));

		long start = System.currentTimeMillis();
		servlet.service(req, res);
		long end = System.currentTimeMillis();
		
		Log.write("WAS", servlet, end - start, "");
	}
	
	/**
	 * 
	 * @param req
	 * @return
	 */
	private static String makeLogMsg(ServletRequest req) {
		
		// 입력값 검증
		if(req == null) {
			return "";
		}
		
		if(req instanceof HttpServletRequest == false) {
			return "";
		}
		
		// 메시지 생성
		StringBuilder msg = new StringBuilder("");
		
		HttpServletRequest httpReq = (HttpServletRequest)req;
		msg.append(httpReq.getRequestURI());	// 요청 URL 추가
		
		return msg.toString();
	}
}
