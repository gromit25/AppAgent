package com.babe.agent.transform;

/**
 * 
 * @author jmsohn
 */
class JoinPointInfo {
	
	private String className;
	private String methodName;
	private int lineNumber;
	
	JoinPointInfo(final String className, final String methodName, final int lineNumber) throws Exception {

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
	boolean isAcceptable(final String className, final String methodName, final int lineNumber) {
		
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

	String getClassName() {
		return this.className;
	}

	String getMethodName() {
		return this.methodName;
	}

	int getLineNumber() {
		return this.lineNumber;
	}
}
