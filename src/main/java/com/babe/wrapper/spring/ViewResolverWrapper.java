package com.babe.wrapper.spring;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class ViewResolverWrapper {
	
	public static View resolveViewName(ViewResolver resolver, String viewName, Locale locale) throws Exception {
		
		System.out.println("#### view name : " + viewName + "\t" + resolver.toString());
		View view = resolver.resolveViewName(viewName, locale);
		
		return view;
	}

}
