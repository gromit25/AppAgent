package com.redeye.appagent.builtins.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;

/**
 * 
 * 
 * @author jmsohn
 */
@TargetClass(type = "DB", cls = "javax/sql/DataSource")
public class DataSourceWrapper {

	/**
	 * 
	 * 
	 * @param ds
	 * @return
	 */
	@TargetMethod("getConnection()Ljava/sql/Connection;")
	public static Connection getConnection(DataSource ds) throws SQLException {
		return getConnectionWrapper(ds.getConnection());
	}
	
	/**
	 * 
	 * 
	 * @param ds
	 * @param username
	 * @param password
	 * @return
	 */
	@TargetMethod("getConnection(Ljava/lang/String;Ljava/lang/String;)Ljava/sql/Connection;")
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
