package com.redeye.appagent.builtins.db;

import java.sql.Statement;
import java.sql.ResultSet;

@TargetClass("java/sql/Statement")
public class PreparedStatementWrapper {
	
	@TargetMethod("setString(ILjava/lang/String;)V")
	public static void setString(PreparedStatement pstmt, int parameterIndex, String x) throws SQLException {

		pstmt.setString(parameterIndex, x);
		ContentsDB.addParam(x);
	}

	@TargetMethod("setInt(II)V")
	public static void setInt(PreparedStatement pstmt, int parameterIndex, int x) throws SQLException {

		pstmt.setInt(parameterIndex, x);
		ContentsDB.addParam(x);
	}

	@TargetMethod("execute()Z")
	public static boolean execute(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		boolean result = pstmt.execute();
		long end = System.currentTimeMillis();

		Log.write(ActionType.DB_SQL.name(), pstmt, end-start, "\"sql\": \"%s\", \"params\": \"%s\"", ContentsDB.getSql(), ConstantsDB.getParams());
		ContentsDB.clear();

		return result;
	}
	
	@TargetMethod("executeUpdate()I")
	public static int executeUpdate(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		int result = pstmt.executeUpdate();
		long end = System.currentTimeMillis();

		Log.write(ActionType.DB_UPD.name(), pstmt, end-start, "\"sql\": \"%s\", \"params\": \"%s\"", ContentsDB.getSql(), ConstantsDB.getParams());
		ContentsDB.clear();
		
		return result;
	}

	@TargetMethod("executeQuery()Ljava/sql/ResultSet;")
	public static ResultSet executeQuery(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		ResultSet rs = pstmt.executeQuery();
		long end = System.currentTimeMillis();

		Log.write(ActionType.DB_SEL.name(), pstmt, end-start, "\"sql\": \"%s\", \"params\": \"%s\"", ContentsDB.getSql(), ConstantsDB.getParams());
		ContentsDB.clear();

		return rs;
	}
}
