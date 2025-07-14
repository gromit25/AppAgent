package com.redeye.appagent.builtins.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

@TargetClass(type = "DB", cls = "java/sql/Connection")
public class ConnectionWrapper {

	@TargetMethod("createStatement()Ljava/sql/Statement;")
	public static Statement createStatement() throws SQLException {

		Statement stmt = conn.createStatement();
		return stmt;
	}
	
	@TargetMethod("prepareStatement(Ljava/lang/String;)Ljava/sql/PreparedStatement;")
	public static PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {

		ContentsDB.setSql(sql);
		Log.write(ActionType.DB_SQL.name(), null, "\"sql\": \"%s\"", sql);
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		return pstmt;
	}
}
