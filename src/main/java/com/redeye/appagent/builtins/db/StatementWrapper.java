package com.redeye.appagent.builtins.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

/**
 * 
 * 
 * @author jmsohn
 */
@TargetClass(type = "DB", cls = "java/sql/Statement")
public class StatementWrapper {

	/**
	 * 
	 * 
	 * @param stmt
	 * @param sql
	 * @return
	 */
	@TargetMethod("executeQuery(Ljava/lang/String;)Ljava/sql/ResultSet;")
	public static ResultSet executeQuery(Statement stmt, String sql) throws SQLException {
		
		ResultSet result = null;

		BuiltinsUtil.measureExecTime(
			() -> {
				result = stmt.executeQuery(sql);
			}
		);
		
		Log.write(ActionType.DB_SEL.name(), stmt, end-start, "\"sql\": \"%s\"", sql);
		
		return result;
	}
}
