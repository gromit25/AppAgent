package com.babe.agent.transform;

import java.util.ArrayList;

/**
 * API 호출 변환 맵 클래스
 * API 호출 메소드 간의 변환 관계를 가지고 있음
 * ex) java/lang/Runtime.exec(Ljava/lang/String;)Ljava/lang/Process;
 *     -> com/babe/wrapper/RuntimeWrapper.exec(Ljava/lang/Runtime;Ljava/lang/String;)Ljava/lang/Process;
 * @author jmsohn
 */
public final class TransformMap {
	
	// ----- properties --------
	/**
	 * 변환할 클래스인지 확인용 문자열 목록
	 * 목록에 포함되어 있으면, 현재 변환 맵을 이용하여 변환 수행
	 */
	private ArrayList<JoinPointInfo> joinPointIncludes;
	/**
	 * 변환할 클래스인지 확인용 문자열 목록
	 * 목록에 포함되어 있지 않으면, 현재 변환 맵을 이용하여 변환 수행
	 */
	private ArrayList<JoinPointInfo> joinPointExcludes;
	
	/** 수정 대상 API 메소드의 클래스 명*/
	private String targetAPIClass;
	/** 수정 대상 API 메소드의 메소드 명*/
	private String targetAPIMethod;
	/** 수정 대상 API 메소드의 Signature*/
	private String targetAPISignature;
	
	/** 대체 API 메소드의 클래스 명*/
	private String altAPIClass;
	/** 대체 API 메소드의 메소드 명*/
	private String altAPIMethod;
	/** 대체 API 메소드의 Signature*/
	private String altAPISignature;
	
	// ----- method -----
	
	/**
	 * map에 저장되는 key 값을 만들기 위해,
	 * 클래스명, 메소드명, 시그니처를 이용해 하나의 스트링으로 만든다.
	 * ex) 클래스명.메소드명시그니쳐
	 * 클래스명 : java/lang/Runtime
	 * 메소드명 : exec
	 * 시그니쳐 : (Ljava/lang/String;)Ljava/lang/Process;
	 * -> java/lang/Runtime.exec(Ljava/lang/String;)Ljava/lang/Process;
	 * @param className 클래스명
	 * @param methodName 메소드명
	 * @param signature 시그니처
	 * @return 
	 */
	public static String makeFullName(final String className, final String methodName, final String signature) {
		
		StringBuilder fullNameBuilder = new StringBuilder("");
		fullNameBuilder.append(className);
		
		// NEW 연산의 경우,
		// 메소드 명과 시그니쳐가 없다. 클래스명만 리턴
		if(methodName == null || signature == null) {
			return fullNameBuilder.toString();
		}
		
		fullNameBuilder.append(".").append(methodName).append(signature);
		
		return fullNameBuilder.toString();
	}
	
	// ------------------
	
	/**
	 * 생성자
	 * 다른 패키지에서 직접 생성 못하게 package 범위로 설정함
	 * readConfig 메소드로만 생성가능하도록 함
	 */
	TransformMap() {
	}

	/**
	 * 수정 대상 메소드의 전체 이름
	 * @return 수정 대상 메소드의 전체 이름
	 */
	String getTargetAPI() {		
		return TransformMap.makeFullName(this.getTargetAPIClass(), this.getTargetAPIMethod(), this.getTargetAPISignature());
	}
	
	/**
	 * 변환 메소드의 전체 이름
	 * @return 변환 메소드의 전체 이름
	 */
	String getAltAPI() {		
		return TransformMap.makeFullName(this.getAltAPIClass(), this.getAltAPIMethod(), this.getAltAPISignature());
	}
	
	/**
	 * API 변환 수행 여부
	 * @param className 클래스명
	 * @param methodName 메소드명
	 * @param lineNumber 라인번호
	 * @return API 변환 수행 여부
	 */
	boolean isTransform(final String className, final String methodName, final int lineNumber) {
		
		// 입력값 검사
		if(className == null || methodName == null) {
			return false;
		}
		
		// 1. exclude
		for(JoinPointInfo exclude : this.getJoinPointExcludes()) {
			if(exclude.isAcceptable(className, methodName, lineNumber) == true) {
				return false;
			}
		}
		
		// 2. include
		if(this.getJoinPointIncludes().size() != 0) {
			
			for(JoinPointInfo include : this.getJoinPointIncludes()) {
				if(include.isAcceptable(className, methodName, lineNumber) == true) {
					return true;
				}
			}
			
			return false;
		} else {
			return true;
		}

	}
	
	// ----- gettter & setter -------
	
	public String getTargetAPIClass() {
		return this.targetAPIClass;
	}
	
	void setTargetAPIClass(final String targetAPIClass) {
		this.targetAPIClass = targetAPIClass;
	}
	
	public String getTargetAPIMethod() {
		return this.targetAPIMethod;
	}
	
	void setTargetAPIMethod(final String targetAPIMethod) {
		this.targetAPIMethod = targetAPIMethod;
	}
	
	public String getTargetAPISignature() {
		return this.targetAPISignature;
	}
	
	void setTargetAPISignature(final String targetAPISignature) {
		this.targetAPISignature = targetAPISignature;
	}
	
	public String getAltAPIClass() {
		return this.altAPIClass;
	}
	
	void setAltAPIClass(final String altAPIClass) {
		this.altAPIClass = altAPIClass;
	}
	
	public String getAltAPIMethod() {
		return this.altAPIMethod;
	}
	
	void setAltAPIMethod(final String altAPIMethod) {
		this.altAPIMethod = altAPIMethod;
	}
	
	public String getAltAPISignature() {
		return this.altAPISignature;
	}
	
	void setAltAPISignature(final String altAPISignature) {
		this.altAPISignature = altAPISignature;
	}

	public ArrayList<JoinPointInfo> getJoinPointIncludes() {
		
		if(this.joinPointIncludes == null) {
			this.joinPointIncludes = new ArrayList<JoinPointInfo>();
		}
		
		return this.joinPointIncludes;
	}

	public void setJoinPointIncludes(ArrayList<JoinPointInfo> joinPointIncludes) {
		this.joinPointIncludes = joinPointIncludes;
	}

	public ArrayList<JoinPointInfo> getJoinPointExcludes() {
		
		if(this.joinPointExcludes == null) {
			this.joinPointExcludes = new ArrayList<JoinPointInfo>();
		}
		
		return this.joinPointExcludes;
	}

	public void setJoinPointExcludes(ArrayList<JoinPointInfo> joinPointExcludes) {
		this.joinPointExcludes = joinPointExcludes;
	}
}
