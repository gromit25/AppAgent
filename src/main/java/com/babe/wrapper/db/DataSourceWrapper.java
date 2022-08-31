package com.babe.wrapper.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DataSourceWrapper {
	
	public static Connection getConnection(DataSource ds) throws SQLException {
		return getConnectionWrapper(ds.getConnection());
	}
	
	public static Connection getConnection(DataSource ds, String username, String password) throws SQLException {
		return getConnectionWrapper(ds.getConnection(username, password));
	}
	
	/**
	 * 
	 * @param conn
	 * @return
	 */
	private static Connection getConnectionWrapper(Connection conn) throws SQLException {
		
		if(conn instanceof ConnectionWrapper) {
			
			// Connection 이 이미 ConnectionWrapper 객체 일때,
			// 따로 Wrapper를 다시 씌우지 않음
			return conn;
			
		} else {
			
			Connection connWrapper = new ConnectionWrapper(conn);
			return connWrapper;
			
		}
	}
}
