package com.babe.collectionwatcher;

abstract class CollectionInfo<T> {
	
	private String createClassName;
	private String createMethodName;
	private int createLoc;
	
	abstract void setCollectionObj(T collectionObj);
	abstract T getCollectionObj();
	abstract int size();
	
	String getCreateClassName() {
		return this.createClassName;
	}
	
	void setCreateClassName(final String createClassName) {
		this.createClassName = createClassName;
	}
	
	String getCreateMethodName() {
		return this.createMethodName;
	}
	
	void setCreateMethodName(final String createMethodName) {
		this.createMethodName = createMethodName;
	}
	
	int getCreateLoc() {
		return this.createLoc;
	}
	
	void setCreateLoc(final int createLoc) {
		this.createLoc = createLoc;
	}
}
