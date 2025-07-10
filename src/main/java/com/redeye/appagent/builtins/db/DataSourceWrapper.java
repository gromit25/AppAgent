package com.redeye.appagent.builtins.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.redeye.appagent.annotation.TargetClass;
import com.redeye.appagent.annotation.TargetMethod;
import com.redeye.appagent.logger.Log;

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
	 * @param conn
	 */
	private static void logConn(Connection conn, String username) {
		
		Log.write(
			ActionType.DB_CON.name(),
			conn,
			"\"username\":\"%s\"", username
		);
	}

	/**
	 * 
	 * 
	 * @param ds
	 * @return
	 */
	@TargetMethod("getConnection()Ljava/sql/Connection;")
	public static Connection getConnection(DataSource ds) throws SQLException {
		
		Connection conn = ds.getConnection();
		logConn(conn, "N/A");
		
		return conn;
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
		
		Connection conn = ds.getConnection(username, password);
		logConn(conn, username);
		
		return conn;
	}
}
