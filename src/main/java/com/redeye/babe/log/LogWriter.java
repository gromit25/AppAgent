package com.redeye.babe.log;

/**
 * 
 * 
 * @author jmsohn
 */
public interface LogWriter {
	
	/**
	 * 
	 * @param msg
	 */
	public void write(String msg) throws Exception;
}
