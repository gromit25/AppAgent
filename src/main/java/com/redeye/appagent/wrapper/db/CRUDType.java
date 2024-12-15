package com.redeye.appagent.wrapper.db;

/**
 * CRUD 종류
 * @author jmsohn
 */
public enum CRUDType {
	
	INSERT("C"),
	UPDATE("U"),
	DELETE("D"),
	MERGE("CUD"),
	UPSERT("CU"),
	SELECT("R");
	
	//---------------
	/** CRUD 종류 코드 */
	private String typeCode;
	
	/**
	 * 생성자
	 * @param typeCode CRUD 종류 코드
	 */
	private CRUDType(String typeCode) {
		this.typeCode = typeCode;
	}
	
	/**
	 * 현재 CRUD 타입의 종류 코드를 리턴함
	 * @return CRUD 종류 코드
	 */
	public String getTypeCode() {
		return this.typeCode;
	}
}
