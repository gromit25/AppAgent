package com.redeye.appagent.wrapper.test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.ApiType;

@TargetClass(cls = "com/test/A", type = ApiType.NONE)
public class ClassAWrapper {
	
	private static MethodHandle sayHello;
	
	@TargetMethod("sayHello(Ljava/lang/String;)V")
	public static void sayHello(String name) throws Throwable {
		
		System.out.println("#### DEBUG in Wrapper ####");
		
		if(sayHello == null) {
			Class<?> classA = Class.forName("com.test.A");
			
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			sayHello = lookup.findStatic(
				classA,
				"sayHello",
				MethodType.methodType(void.class, String.class)
			);
		}

		long start = System.currentTimeMillis();
		sayHello.invokeExact(name);
		System.out.println("Elapsed Time:" + (System.currentTimeMillis() - start));
	}
}
