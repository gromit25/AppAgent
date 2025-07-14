package com.redeye.appagent.builtins.db;

import java.sql.Connection;
import java.sql.PreparedStatement;

@TargetClass(type = "DB", cls = "java/sql/Connection")
public class ConnectionWrapper {

	@TargetMethod("prepareStatement(Ljava/lang/String;)Ljava/sql/PreparedStatement;")
	public static PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {

		ContentsDB.setSql(sql);
		
		PreparedStatement pstmt = conn.prepareStatement(sql);
		return pstmt;
	}
}
