package com.redeye.appagent.builtins.spring;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.web.bind.annotation.GetMapping;

import com.redeye.appagent.annotation.ApiDesc;
import com.redeye.appagent.annotation.JoinAdvice;
import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;

/**
 * 
 * 
 * @author jmsohn
 */
@TargetClass(type="SPR", cls = "java/lang/reflect/Method")
public class ApiInvokeWrapper {
	
	/**
	 * 
	 * @param method
	 */
	private static void logInvoke(Method method) {
		
		ApiDesc apiDesc = method.getAnnotation(ApiDesc.class);
		if(apiDesc == null) {
			return;
		}
		
		System.out.println("API DESCRIPTION =====");
		System.out.println(apiDesc.id());
		System.out.println(apiDesc.name());
		System.out.println(apiDesc.desc());
		
		GetMapping mapping = method.getAnnotation(GetMapping.class);
		if(mapping != null) {
			for(String urlStr : mapping.value()) {
				System.out.println("url:" + urlStr);
			}
		}
	}

	/**
	 * 
	 * 
	 * @param method
	 * @param obj
	 * @param args
	 * @return
	 */
	@TargetMethod("invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
	@JoinAdvice("org/springframework/web/method/support/InvocableHandlerMethod.doInvoke")
    public static Object invoke(Method method, Object obj, Object... args)
    		throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		logInvoke(method);
    	Object result = method.invoke(obj, args);
    	
    	return result;
	}
}
