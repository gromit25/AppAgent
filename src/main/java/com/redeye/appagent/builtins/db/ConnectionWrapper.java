package com.redeye.appagent.builtins.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;

/**
 * DB Connection Wrapper
 * 
 * @author jmsohn
 */
@TargetClass(type = "DB", cls = "java/sql/Connection")
public class ConnectionWrapper {

	/**
	 * prepareStatement 메소드 Wrapper
	 * 
	 * @param conn DB Connection
	 * @param sql 쿼리
	 * @return 생성된 PreparedStatement
	 */
	@TargetMethod("prepareStatement(Ljava/lang/String;)Ljava/sql/PreparedStatement;")
	public static PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		DBContents.setSql(sql);
		
		return pstmt;
	}
}
