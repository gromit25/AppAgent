package com.redeye.appagent.builtins.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

@TargetClass(type="DB", cls="java/sql/Statement")
public class PreparedStatementWrapper {
	
	@TargetMethod("setString(ILjava/lang/String;)V")
	public static void setString(PreparedStatement pstmt, int parameterIndex, String x) throws SQLException {

		pstmt.setString(parameterIndex, x);
		DBContents.addParam(x);
	}

	@TargetMethod("setInt(II)V")
	public static void setInt(PreparedStatement pstmt, int parameterIndex, int x) throws SQLException {

		pstmt.setInt(parameterIndex, x);
		DBContents.addParam(Integer.toString(x));
	}

	@TargetMethod("setLong(IJ)V")
	public static void setLong(PreparedStatement pstmt, int parameterIndex, long x) throws SQLException {

		pstmt.setLong(parameterIndex, x);
		DBContents.addParam(Long.toString(x));
	}

	@TargetMethod("setFloat(IF)V")
	public static void setLong(PreparedStatement pstmt, int parameterIndex, float x) throws SQLException {

		pstmt.setFloat(parameterIndex, x);
		DBContents.addParam(Float.toString(x));
	}

	@TargetMethod("setDouble(ID)V")
	public static void setDouble(PreparedStatement pstmt, int parameterIndex, double x) throws SQLException {

		pstmt.setDouble(parameterIndex, x);
		DBContents.addParam(Double.toString(x));
	}

	@TargetMethod("execute()Z")
	public static boolean execute(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		boolean result = pstmt.execute();
		long end = System.currentTimeMillis();

		Log.write(
			ActionType.DB_SQL.name(), pstmt, end-start,
			"\"sql\": \"%s\", \"params\": \"%s\"", DBContents.getSql(), DBContents.getParams()
		);
		DBContents.clear();

		return result;
	}
	
	@TargetMethod("executeUpdate()I")
	public static int executeUpdate(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		int result = pstmt.executeUpdate();
		long end = System.currentTimeMillis();

		Log.write(
			ActionType.DB_UPD.name(), pstmt, end-start,
			"\"sql\": \"%s\", \"params\": \"%s\"", DBContents.getSql(), DBContents.getParams()
		);
		DBContents.clear();
		
		return result;
	}

	@TargetMethod("executeQuery()Ljava/sql/ResultSet;")
	public static ResultSet executeQuery(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		ResultSet rs = pstmt.executeQuery();
		long end = System.currentTimeMillis();

		Log.write(
			ActionType.DB_SEL.name(), pstmt, end-start,
			"\"sql\": \"%s\", \"params\": \"%s\"", DBContents.getSql(), DBContents.getParams()
		);
		DBContents.clear();

		return rs;
	}
}
