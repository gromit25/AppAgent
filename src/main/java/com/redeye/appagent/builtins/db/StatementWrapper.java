package com.redeye.appagent.builtins.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

/**
 * Statement Wrapper 클래스
 * 
 * @author jmsohn
 */
@TargetClass(type = "DB", cls = "java/sql/Statement")
public class StatementWrapper {

	/**
	 * executeQuery Wrapper 메소드
	 * 
	 * @param stmt Statement 객체
	 * @param sql 쿼리
	 * @return 쿼리 결과
	 */
	@TargetMethod("executeQuery(Ljava/lang/String;)Ljava/sql/ResultSet;")
	public static ResultSet executeQuery(Statement stmt, String sql) throws SQLException {
		
		return BuiltinsUtil.logExecTime(
			ActionType.DB_SEL.name(),
			stmt,
			String.format("\"sql\": \"%s\"", sql),
			() -> {
				return stmt.executeQuery(sql);
			}
		);
	}
}
