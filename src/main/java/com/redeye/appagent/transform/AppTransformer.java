package com.redeye.appagent.transform;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.Set;

import com.redeye.appagent.Config;
import com.redeye.appagent.wrapper.db.PreparedStatementWrapper;

/**
 * API 호출부를 모니터링 메소드 호출로 변환하는 클래스
 * 
 * @author jmsohn
 */
public final class AppTransformer implements ClassFileTransformer {
	
	/** 전체 스킵 여부(테스트용) */
	private boolean isSkip = false;
	
	/** 조인 패키지 목록 */
	private Set<String> joinPackageSet;
	
	/**
	 * 생성자
	 */
	public AppTransformer() throws Exception {
		
		// 조인 패키지 목록 설정
		this.joinPackageSet = new HashSet<>();
		for(String joinPackage: Config.JOIN_PACKAGE.getValue().split(",")) {
			this.joinPackageSet.add(joinPackage.trim());
		}
	}

	@Override
	public byte[] transform(
		ClassLoader loader, String className, Class<?> classBeingRedefined,
		ProtectionDomain protectionDomain, byte[] classfileBuffer
	) throws IllegalClassFormatException {
		
		// 클래스 변환 작업 수행 후 변환된 클래스 반환
		return this.transformAPI(className, protectionDomain, classfileBuffer);
	}
	
	/**
	 * 주어진 클래스를 변환 맵에 따라 바이트 코드 변환 후 반환
	 * 
	 * @param className 변환할 클래스 명
	 * @param protectionDomain 
	 * @param classfileBuffer 
	 * @return 변환된 바이트 코드
	 */
	public byte[] transformAPI(
		String className,
		ProtectionDomain protectionDomain,
		byte[] classfileBuffer
	)throws IllegalClassFormatException {

		try {
			
			//
			if(this.isSkip(className, protectionDomain) == true) {
				return classfileBuffer;
			}
			
	        // 변환된 바이트 코드 반환
	        return classGen.getJavaClass().getBytes();

		} catch(Exception ex) {

			// 예외 발생시 원본 bytecode 반환
			return classfileBuffer;
		}
	}
	
	/**
	 * 주어진 클래스 변환 여부 반환
	 * 
	 * @param className
	 * @param protectionDomain
	 * @return 변경 여부(스킵시 true, 변환시 false)
	 */
	private boolean isSkip(final String className, final ProtectionDomain protectionDomain) {
		
		//---------------------
		// isSkip 이 설정되어 있으면 전체 스킵
		if(this.isSkip == true) {
			return true;
		}
		
		// class 명이 없거나
		// AppAgent의 클래스이면 스킵
		if(
			className == null
			|| className.startsWith(Config.AGENT_PACKAGE.getValue()) == true
		) {
			return true;
		}
		
		//---------------------
		// protection domain이 null인 것은 boot library 이므로 스킵
		// 아닐 경우 스킵하지 않도록 함
		if(protectionDomain == null || protectionDomain.getCodeSource() == null ) {
			return true;
		}

		//---------------------
		// 클래스에 join 조인 여부 반환
		for(String joinPackage: this.joinPackageSet) {
			if(className.startsWith(joinPackage) == true) {
				return true;
			}
		}
		
		return false;
	}
}
