package com.redeye.appagent.builtins.db;

import java.sql.Statement;
import java.sql.ResultSet;

@TargetClass("java/sql/Statement")
public class StatementWrapper {

	@TargetMethod("executeQuery(Ljava/lang/String;)Ljava/sql/ResultSet;")
  public static ResultSet executeQuery(Statement stmt, String sql) throws SQLException {

    long start = System.currentTimeMillis();
    ResultSet result = stmt.executeQuery(sql);
    long end = System.currentTimeMillis();

    Log.write(ActionType.DB_SEL.name(), stmt, end-start, "\"sql\": \"%s\"", sql);
    
    return result;
  }
}
