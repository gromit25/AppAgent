package com.babe.wrapper.db;

import java.util.HashSet;
import java.util.Hashtable;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;

public class CRUDTableCollector extends FromItemVisitorAdapter {
	
	private Hashtable<CRUDType, HashSet<String>> tableNames;
	
	public void insertTableName(CRUDType crudType, String tableName) {
		this.getTableNames().get(crudType).add(tableName);
	}
	
	@Override
	public void visit(Table tableName) {
		this.insertTableName(CRUDType.SELECT, tableName.getFullyQualifiedName());
		System.out.println("DEBUG SELECT 100 : " + tableName.getFullyQualifiedName());
	}
	
	public HashSet<String> getTableNamesByCRUDTypes(CRUDType crudType) {
		return this.getTableNames().get(crudType);
	}

	public Hashtable<CRUDType, HashSet<String>> getTableNames() {
		
		if(this.tableNames == null) {
			
			// 초기화
			this.tableNames = new Hashtable<CRUDType, HashSet<String>>();
			
			for(CRUDType crudType: CRUDType.values()) {
				this.tableNames.put(crudType, new HashSet<String>());
			}
			
		}
		
		return this.tableNames;
	}
}
