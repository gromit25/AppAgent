package com.redeye.appagent.builtins.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

/**
 * 
 * 
 * @author jmsohn
 */
@TargetClass(type = "DB", cls = "java/sql/Statement")
public class PreparedStatementWrapper {
	
	/**
	 * 
	 * 
	 * @param pstmt
	 * @param parameterIndex
	 * @param x
	 */
	@TargetMethod("setString(ILjava/lang/String;)V")
	public static void setString(PreparedStatement pstmt, int parameterIndex, String x) throws SQLException {

		pstmt.setString(parameterIndex, x);
		DBContents.addParam(x);
	}

	/**
	 * 
	 * @param pstmt
	 * @param parameterIndex
	 * @param x
	 */
	@TargetMethod("setInt(II)V")
	public static void setInt(PreparedStatement pstmt, int parameterIndex, int x) throws SQLException {

		pstmt.setInt(parameterIndex, x);
		DBContents.addParam(Integer.toString(x));
	}

	/**
	 * 
	 * 
	 * @param pstmt
	 * @param parameterIndex
	 * @param x
	 */
	@TargetMethod("setLong(IJ)V")
	public static void setLong(PreparedStatement pstmt, int parameterIndex, long x) throws SQLException {

		pstmt.setLong(parameterIndex, x);
		DBContents.addParam(Long.toString(x));
	}

	/**
	 * 
	 * 
	 * @param pstmt
	 * @param parameterIndex
	 * @param x
	 */
	@TargetMethod("setFloat(IF)V")
	public static void setLong(PreparedStatement pstmt, int parameterIndex, float x) throws SQLException {

		pstmt.setFloat(parameterIndex, x);
		DBContents.addParam(Float.toString(x));
	}

	/**
	 * 
	 * 
	 * @param pstmt
	 * @param parameterIndex
	 * @param x
	 */
	@TargetMethod("setDouble(ID)V")
	public static void setDouble(PreparedStatement pstmt, int parameterIndex, double x) throws SQLException {

		pstmt.setDouble(parameterIndex, x);
		DBContents.addParam(Double.toString(x));
	}

	/**
	 * 
	 * 
	 * 
	 * @param pstmt
	 * @return
	 */
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

	/**
	 *
	 * 
	 * @param pstmt
	 * @return
	 */
	@TargetMethod("executeUpdate()I")
	public static int executeUpdate(PreparedStatement pstmt) throws SQLException {

		long start = System.currentTimeMillis();
		int result = pstmt.executeUpdate();
		long end = System.currentTimeMillis();

		Log.write(
			ActionType.DB_CUD.name(), pstmt, end-start,
			"\"sql\": \"%s\", \"params\": \"%s\"", DBContents.getSql(), DBContents.getParams()
		);
		DBContents.clear();
		
		return result;
	}

	/**
	 * 
	 * 
	 * @param pstmt
	 * @return
	 */
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
