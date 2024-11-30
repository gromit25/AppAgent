package com.redeye.babe.wrapper.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC 드라이버 관리자(java.sql.DriverManager)의 Wrapper클래스
 * @author jmsohn
 */
public class DriverManagerWrapper {
	
	/**
	 * java.sql.DriverManager 객체의 getConnection Wrapper 메소드
	 * @param url 데이터베이스 연결 url
	 * @param user 접속 사용자
	 * @param password 접속 패스워드
	 */
	public static Connection getConnection(String url, String user, String password) throws SQLException {
		
		System.out.println("Wrapper 010:");
		
		Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
	}
	
	/**
	 * java.sql.DriverManager 객체의 getConnection Wrapper 메소드
	 * @param url 데이터베이스 연결 url
	 */
	public static Connection getConnection(String url) throws SQLException {
		
		System.out.println("Wrapper 020:");
		
		Connection conn = DriverManager.getConnection(url);
		return conn;
	}

}
