package com.redeye.appagent.script.parserfw;

import java.util.ArrayList;

/**
 * 전이함수 생성(Builder) 클래스
 * 
 * @author jmsohn
 */
public class TransferBuilder {
	
	/** 전이함수의 명세(specification) 목록 */
	private ArrayList<Object[]> specs;
	
	/**
	 * 생성자
	 */
	public TransferBuilder() {
		this.specs = new ArrayList<Object[]>();
	}
	
	/**
	 * 전이함수 명세 추가
	 * 
	 * @param pattern 전이가 발생하는 character 종류
	 * @param nextStatus 전이 상태
	 * @param bushback 전이가 발생할 시 pushback될 숫자
	 * @return 현재 객체(fluent 코딩용)
	 */
	public TransferBuilder add(String pattern, String nextStatus, int pushback) throws Exception {
		
		Object[] spec = new Object[3];
		spec[0] = pattern;
		spec[1] = nextStatus;
		spec[2] = pushback;
		if(pushback > 0) {
			throw new Exception("pushback value must be less than 0");
		}
		
		specs.add(spec);
		
		return this;
	}
	
	/**
	 * 전이함수 명세 추가
	 * 
	 * @param pattern 전이가 발생하는 character 종류
	 * @param nextStatus 전이 상태
	 * @return 현재 객체(fluent 코딩용)
	 */
	public TransferBuilder add(String pattern, String nextStatus) throws Exception {
		return this.add(pattern, nextStatus, 0);
	}

	
	/**
	 * 설정된 전이함수 명세를 이용하여 전이함수 객체(Transfer) 생성
	 * 
	 * @return 생성된 전이함수 객체 목록
	 */
	public ArrayList<Transfer> build() throws Exception {
		
		ArrayList<Transfer> transferFunctions = new ArrayList<Transfer>();
		
		for(Object[] spec: this.specs) {
			Transfer transfer = new Transfer(spec[0].toString(), spec[1].toString(), (int)spec[2]);
			transferFunctions.add(transfer);
		}
		
		return transferFunctions;
	}
}