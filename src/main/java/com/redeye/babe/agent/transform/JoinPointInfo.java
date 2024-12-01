package com.redeye.babe.agent.transform;

import lombok.Data;

/**
 * 
 * @author jmsohn
 */
@Data
class JoinPointInfo {

	/** */
	private String className;
	
	/** */
	private String methodName;
	
	/** */
	private int lineNumber;
	
	
	/**
	 * 생성자
	 *  
	 * @param className
	 * @param methodName
	 * @param lineNumber
	 */
	JoinPointInfo(String className, String methodName, int lineNumber) throws Exception {

		if(className == null) {
			throw new Exception("transform target class name is null.");
		}
		
		this.className = className;
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}
	
	/**
	 * 
	 * @param className
	 * @param methodName
	 * @param lineNumber
	 * @return
	 */
	boolean isAcceptable(String className, String methodName, int lineNumber) {
		
		if(className == null || methodName == null) {
			return false;
		}
		
		if(this.getLineNumber() > 0 && this.getLineNumber() != lineNumber) {
			return false;
		}
		
		if(this.getMethodName() != null && this.getMethodName().equals(methodName) == false) {
			return false;
		}
		
		if(className.contains(this.getClassName()) == false) {
			return false;
		}
		
		return true;
	}
}
