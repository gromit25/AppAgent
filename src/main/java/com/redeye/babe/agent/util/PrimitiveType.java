package com.redeye.babe.agent.util;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 기본형 타입
 * 
 * @author jmsohn
 */
public enum PrimitiveType {
	
	VOID("void", "V"),
	BOOLEAN("boolean", "Z"),
	CHAR("char", "C"),
	BYTE("byte", "B"),
	SHORT("short", "S"),
	INT("int", "I"),
	FLOAT("float", "F"), 
	LONG("long", "J"),
	DOUBLE("double", "D");
	
	// ----------------------------------------
	
	/**
	 * name(key)-type(value) Map 변수<br>
	 * name 으로 type 을 검색하지 위한 용도
	 */
	private static Map<String, PrimitiveType> typeMap;
	
	// name-type Map 초기화
	static {
		
		typeMap = new HashMap<>();
		
		for(PrimitiveType type: PrimitiveType.values()) {
			typeMap.put(type.getName(), type);
		}
	}
	
	/**
	 * 기본형 이름에 해당하는 Primitive Type 반환<br>
	 * 없는 경우 null 을 반환
	 * 
	 * @param name 기본형의 이름
	 * @return 기본형 이름에 해당하는 Primitive Type
	 */
	public static PrimitiveType get(String name) {
		
		// name-type map 이 없을 경우, null 을 반환
		if(typeMap == null) return null;
		
		// name 에 해당하는 기본형 반환
		return typeMap.get(name);
	}

	// ----------------------------------------
	
	/** 기본형 이름 */
	@Getter
	private String name;
	
	/** 기본형 바이트 코드 */
	@Getter
	private String code;
	
	/**
	 * 생성자
	 * 
	 * @param name 기본형 이름
	 * @param code 기본형 바이트 코드
	 */
	private PrimitiveType(String name, String code) {
		this.name = name;
		this.code = code;
	}
}
