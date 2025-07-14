package com.redeye.appagent.builtins.db;

@TargetClass(type = "DB", cls = "java/sql/PreparedStatement")
public class PreparedStatementWrapper {
	
	@TargetMethod("prepareStatement(Ljava/lang/String;)Ljava/sql/PreparedStatement;")
	public static PreparedStatement prepareStatement(Connection conn, String sql) throws SQLException {

		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		return pstmt;
	}
}
