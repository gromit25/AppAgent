package com.babe.wrapper.db;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.merge.Merge;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.upsert.Upsert;
import net.sf.jsqlparser.util.TablesNamesFinder;

/**
 * 쿼리에서 CRUD 행위별 테이블명을 검색하는 클래스 
 * @author jmsohn
 */
public class CRUDTableFinder extends TablesNamesFinder {
	
	private CRUDTableCollector crudCollector;
	
    @Override
    public void visit(Insert insert) {
    	super.visit(insert);
    	
    	Table insertTable = insert.getTable();
    	this.getCrudCollector().insertTableName(CRUDType.INSERT, insertTable.getFullyQualifiedName());
    	
    	System.out.println("DEBUG INSERT 100 : " + insertTable.getFullyQualifiedName());
    }
	
    @Override
    public void visit(Update update) {
    	super.visit(update);
    	
    	update.getTables()
    	.forEach( updateTable -> {
    		this.getCrudCollector().insertTableName(CRUDType.UPDATE, updateTable.getFullyQualifiedName());
    		
    		System.out.println("DEBUG UPDATE 100 : " + updateTable.getFullyQualifiedName());
    	});
    }
    
    @Override
    public void visit(Delete delete) {
    	super.visit(delete);
    	
    	Table deleteTable = delete.getTable();
    	this.getCrudCollector().insertTableName(CRUDType.DELETE, deleteTable.getFullyQualifiedName());
    	System.out.println("DEBUG DELETE 100 : " + deleteTable.getFullyQualifiedName());
    }
    
    @Override
    public void visit(Merge merge) {
    	super.visit(merge);
    	
    	Table mergeTable = merge.getTable();
    	this.getCrudCollector().insertTableName(CRUDType.MERGE, mergeTable.getFullyQualifiedName());
    	System.out.println("DEBUG MERGE 100 : " + mergeTable.getFullyQualifiedName());
    }
    
    @Override
    public void visit(Upsert upsert) {
    	super.visit(upsert);
    	
    	Table upsertTable = upsert.getTable();
    	this.getCrudCollector().insertTableName(CRUDType.UPSERT, upsertTable.getFullyQualifiedName());
    	System.out.println("UPSERT UPSERT 100 : " + upsertTable.getFullyQualifiedName());
    }
    
    @Override
    public void visit(PlainSelect plainSelect) {
    	super.visit(plainSelect);
    	
    	/*
    	 * Select 문장의 From 절에 있는 Table 명을 가져온다.
    	 * plainSelect에서 직접 가져 올수 없고 
    	 * 오직 Visitor만 사용해서 가져 올 수 있다.  
    	 */
        if (plainSelect.getFromItem() != null) {
            plainSelect.getFromItem().accept(this.getCrudCollector());
        }

        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                join.getRightItem().accept(this.getCrudCollector());
            }
        }
    }

	public CRUDTableCollector getCrudCollector() {
		if(this.crudCollector == null) {
			this.crudCollector = new CRUDTableCollector();
		}
		return this.crudCollector;
	}

}
