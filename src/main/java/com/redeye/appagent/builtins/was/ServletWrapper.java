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

/**
 * 
 * 
 * @author jmsohn
 */
@TargetClass(cls="jakarta/servlet/Servlet", type="SPR")
public class ServletWrapper {
	
	/**
	 * 
	 * 
	 * @param servlet
	 * @param req
	 * @param res
	 */
	@TargetMethod("service(Ljakarta/servlet/ServletRequest;Ljakarta/servlet/ServletResponse;)V")
	@JoinAdvice("org/apache/catalina/core*")
	public static void service(
		Servlet servlet,
		ServletRequest req,
		ServletResponse res
	) throws ServletException, IOException {

		long start = System.currentTimeMillis();
		servlet.service(req, res);
		long end = System.currentTimeMillis();
		
		Log.write("SPR", servlet, end - start, "");
	}
}
