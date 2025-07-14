package com.redeye.appagent.builtins.db;

@TargetClass("java/sql/Statement")
public class PreparedStatementWrapper {
	@TargetMethod("executeQuery(Ljava/lang/String;)Ljava/sql/ResultSet;")
}
