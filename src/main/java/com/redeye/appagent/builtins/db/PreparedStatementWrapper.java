package com.redeye.appagent.builtins.db;

import java.sql.Statement;

@TargetClass("java/sql/Statement")
public class PreparedStatementWrapper {
	
	@TargetMethod("setString(ILjava/lang/String;)V")
	public static void setString(PreparedStatement pstmt, int parameterIndex, String x) throws SQLException {

		pstmt.setString(parameterIndex, x);
		ContentsDB.addParam(x);
	}

	@TargetMethod("executeUpdate()I")
	public static int executeUpdate(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		int result = pstmt.executeUpdate();
		long end = System.currentTimeMillis();

		Log.write(ActionType.DB_INS.name(), pstmt, end-start, "\"sql\": \"%s\", \"params\": \"%s\"", ConstentsDB.getSql(), ConstantsDB.getParams());
		
		return result;
	}
}
