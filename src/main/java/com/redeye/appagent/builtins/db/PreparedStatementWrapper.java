package com.redeye.appagent.builtins.db;

import java.sql.Statement;

@TargetClass("java/sql/Statement")
public class PreparedStatementWrapper {
	
	@TargetMethod("setString(ILjava/lang/String;)V")
	public static void setString(PreparedStatement pstmt, int parameterIndex, String x) throws SQLException {

		pstmt.setString(parameterIndex, x);
		ContentsDB.addParam(x);
	}
}
