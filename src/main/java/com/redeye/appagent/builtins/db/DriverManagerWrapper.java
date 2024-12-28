package com.redeye.appagent.builtins.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;

/**
 * JDBC 드라이버 관리자(java/sql/DriverManager) Wrapper
 * 
 * @author jmsohn
 */
@TargetClass(type = "DB", cls = "java/sql/DriverManager")
public class DriverManagerWrapper {
	
	/**
	 *  getConnection Wrapper 메소드
	 * 
	 * @param url 데이터베이스 연결 url
	 * @param user 접속 사용자
	 * @param password 접속 패스워드
	 */
	@TargetMethod("getConnection(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;")
	public static Connection getConnection(String url, String user, String password) throws SQLException {
		
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}
	
	/**
	 * getConnection Wrapper 메소드
	 * 
	 * @param url 데이터베이스 연결 url
	 */
	@TargetMethod("getConnection(Ljava/lang/String;)Ljava/sql/Connection;")
	public static Connection getConnection(String url) throws SQLException {
		
		Connection conn = DriverManager.getConnection(url);
		return conn;
	}
}
