package com.redeye.appagent.builtins.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

/**
 * DB DataSource Wrapper
 * 
 * @author jmsohn
 */
@TargetClass(type = "DB", cls = "javax/sql/DataSource")
public class DataSourceWrapper {
	
	/**
	 * DB 연결시 로깅
	 * 
	 * @param conn DB Connection
	 * @param username DB 접속 사용자명
	 */
	private static void logConn(Connection conn, String username) {
		
		Log.write(
			ActionType.DB_CON.name(),
			conn,
			"\"username\":\"%s\"", username
		);
	}

	/**
	 * getConnection Wrapper
	 * 
	 * @param ds Data Source 객체
	 * @return 생성된 DB 연결
	 */
	@TargetMethod("getConnection()Ljava/sql/Connection;")
	public static Connection getConnection(DataSource ds) throws SQLException {
		
		Connection conn = ds.getConnection();
		logConn(conn, "N/A");
		
		return conn;
	}
	
	/**
	 * getConnection Wrapper
	 * 
	 * @param ds Data Source 객체
	 * @param username DB 접속 사용자 명
	 * @param password DB 접속 패스트워드
	 * @return 생성된 DB 연결
	 */
	@TargetMethod("getConnection(Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;")
	public static Connection getConnection(DataSource ds, String username, String password) throws SQLException {
		
		Connection conn = ds.getConnection(username, password);
		logConn(conn, username);
		
		return conn;
	}
}
