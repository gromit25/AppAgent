package com.redeye.appagent.rewriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.redeye.appagent.Config;

/**
 * API 호출부를 모니터링 메소드 호출로 변환하는 클래스
 * 
 * @author jmsohn
 */
public final class AppTransformer implements ClassFileTransformer {
	
	/** 전체 스킵 여부(테스트용) */
	private boolean isSkip = false;
	
	/**
	 * 생성자
	 */
	public AppTransformer() throws Exception {
	}

	@Override
	public byte[] transform(
		ClassLoader loader,
		String className,
		Class<?> classBeingRedefined,
		ProtectionDomain protectionDomain,
		byte[] classfileBuffer
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
			
			// 스킵 여부 검사
			if(this.isSkip(className, protectionDomain) == true) {
				return classfileBuffer;
			}
			
			// 클래스 변환 수행
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			AppClassWriter appWriter = new AppClassWriter(Opcodes.ASM9, cw, className); 
			
			ClassReader cr = new ClassReader(classfileBuffer);
			cr.accept(appWriter, 0);
			
			// 변환된 결과 리턴
			return cw.toByteArray();

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
	private boolean isSkip(
		String className,
		ProtectionDomain protectionDomain
	) {
		
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
		return (protectionDomain == null || protectionDomain.getCodeSource() == null);
	}
}
