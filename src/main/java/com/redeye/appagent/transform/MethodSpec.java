package com.redeye.appagent.transform;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;

import lombok.Data;

/**
 * 메소드 스펙
 * 
 * @author jmsohn
 */
@Data
class MethodSpec {
	
	/** 클래스 명(ex. com.redeye.appagent.AppAgent) */
	private String className;
	
	/** 메소드 명 */
	private String methodName;
	
	/** 메소드 시그니처 */
	private String signature;
	
	/**
	 * invoke 명령어에 호출 메소드 스펙 생성 및 반환
	 * 
	 * @param invoke invoke 명령어
	 * @param cpg 클래스의 상수 풀
	 * @return 메소드 스펙
	 */
	static MethodSpec create(InvokeInstruction invoke, ConstantPoolGen cpg) throws Exception {
		
		// 입력값 검증
		if(invoke == null) {
			throw new NullPointerException("invoke instruction is null.");
		}
		
		if(cpg == null) {
			throw new NullPointerException("constant pool is null.");
		}
		
		// 메소드 스펙 생성
		MethodSpec spec = new MethodSpec();
		
		// invoke 명령어에서 메소드 스펙 추출 및 설정
		spec.setClassName(invoke.getClassName(cpg));
		spec.setMethodName(invoke.getMethodName(cpg));
		spec.setSignature(invoke.getSignature(cpg));
		
		return spec;
	}
	
	/**
	 * 클래스의 상수 풀에서 메소드 인덱스를 반환<br>
	 * 없을 경우, 새로운 인덱스를 생성함
	 * 
	 * @param cpg 클래스의 상수 풀
	 * @return 메소드 인덱스
	 */
	int getMethodRef(ConstantPoolGen cpg) throws Exception {
		
		// 메소드의 인덱스를 상수 풀에서 검색
		int methodRef = cpg.lookupMethodref(
			this.getClassName(),
			this.getMethodName(),
			this.getSignature()
		);
		
		// 만일, 메소드의 인덱스가 발견되지 않는 경우(-1)
		// 새로운 인덱스를 생성함
		if(methodRef < 0) {
			methodRef = cpg.addMethodref(
				this.getClassName(),
				this.getMethodName(),
				this.getSignature()
			);
		}
		
		return methodRef;
	}
}
