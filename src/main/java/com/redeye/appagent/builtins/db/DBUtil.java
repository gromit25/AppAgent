package com.redeye.appagent.builtins.db;

import java.sql.SQLException;

import com.redeye.appagent.logger.Log;

public class DBUtil {
	
	/**
	 * 
	 * 
	 * @param apiType
	 * @param obj
	 * @param message
	 * @param method
	 * @return
	 */
	public static <T> T logExecTime(
		String apiType,
		Object obj,
		String message,
		DBMethod<T> method
	) throws SQLException {

		//
		long start = System.currentTimeMillis();
		T result = method.execute();
		long end = System.currentTimeMillis();

		//
		Log.write(apiType, obj, end-start, message);

		//
		return result;
	}
}
