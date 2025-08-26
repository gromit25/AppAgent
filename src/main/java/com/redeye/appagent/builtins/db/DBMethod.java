package com.redeye.appagent.builtins.db;

import java.sql.SQLException;

@FunctionalInterface
public interface DBMethod<T> {
	T execute() throws SQLException;
}
