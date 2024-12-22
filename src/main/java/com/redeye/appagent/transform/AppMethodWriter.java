package com.redeye.appagent.transform;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.redeye.appagent.logger.Log;

import lombok.Getter;

/**
 * 메소드 변환 클래스
 * 
 * @author jmsohn
 */
public class AppMethodWriter extends MethodVisitor {
	
	/** 클래스 명 */
	@Getter
	private String className;
	
	/** 메소드 명 */
	@Getter
	private String methodName;
	
	/** 소스 라인 번호 */
	@Getter
	private int line;
	
	/**
	 * NEW 클래스 명<br>
	 * NEW 명령어는 여러 줄로 실행되기 때문에 임시 저장용 변수<br>
	 * 순서) NEW 객체 생성 -> 생성자 메소드(<init>) 호출
	 */
	private String newClassName;
	
	/**
	 * 생성자
	 * 
	 * @param api API 버전
	 * @param mv 기본 메소드 Visitor
	 * @param className 현재 방문한 클래스 명
	 * @param methodName 현재 방문한 메소드 명
	 */
	AppMethodWriter(int api, MethodVisitor mv, String className, String methodName) {
		
		super(api, mv);
		
		this.className = className;
		this.methodName = methodName;
		this.line = -1;
		
		this.newClassName = null;
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type) {
		
		// NEW 명령어가 아니거나
		// 변환 대상이 아닐 경우
		// 변환하지 않고 반환
		if(
			opcode != Opcodes.NEW ||
			MethodMap.isTargetNew(type) == false
		) {
			
			super.visitTypeInsn(opcode, type);
			return;
		}
		
		// 변환 내용 로깅
		String logMsg = "REPLACE NEW:" + type + " in " + this.className + "." + this.methodName;
		Log.writeAgentLog(logMsg);
		
		// 3바이트 NEW 명령어 -> NOP 3개 명령어 변환
		super.visitInsn(Opcodes.NOP);
		super.visitInsn(Opcodes.NOP);
		super.visitInsn(Opcodes.NOP);
		
		// NEW 클래스 명 설정
		this.newClassName = type;
	}
	
	@Override
	public void visitInsn(int opcode) {
		
		if(opcode == Opcodes.DUP && this.newClassName != null) {
			
			// DUP 명령어 -> NOP 명령어 치환
			super.visitInsn(Opcodes.NOP);
			
		} else {
			
			// 코드 치환하지 않음
			super.visitInsn(opcode);
		}
	}
	
	@Override
	public void visitMethodInsn(
		int opcode,
		String className,
		String methodName,
		String signature,
		boolean isInterface
	) {
		
		// 변환할 메소드 호출이 아닌 경우 반환
		// INVOKESTATIC, INVOKEVIRTURAL, INVOKEINTERFACE, INVOKESPECIAL
		if(Util.isInvokeOp(opcode) == false) {
			super.visitMethodInsn(opcode, className, methodName, signature, isInterface);
			return;
		}
		
		// 변환 메소드 스펙 획득
		MethodSpec altSpec = MethodMap.getAltMethod(className, methodName, signature);
		
		// 변환 대상 메소드가 없는 경우 변환하지 않고 반환
		if(altSpec == null) {
			super.visitMethodInsn(opcode, className, methodName, signature, isInterface);
			return;
		}
		
		//---------------------------------------------------
		// 변환 작업 수행
		// - 변환 내용 로깅 
		String logMsg =
			"REPLACE:"
			+ className + "." + methodName + signature
			+ " in "
			+ this.className + "." + this.methodName;
		Log.writeAgentLog(logMsg);

		// 변환 메소드 호출로 변경
		super.visitMethodInsn(
			Opcodes.INVOKESTATIC,
			altSpec.getClassName(),
			altSpec.getMethodName(),
			altSpec.getSignature(),
			false
		);

		// 호출 명령어 별 후처리
		if(opcode == Opcodes.INVOKEINTERFACE) {
			
			// INVOKEINTERFACE 5바이트를 채우기 위해 NOP 추가
			// INVOKESTATIC은 3바이트임
			super.visitInsn(Opcodes.NOP);
			super.visitInsn(Opcodes.NOP);
			
		} else if(opcode == Opcodes.INVOKESPECIAL && this.newClassName != null) {
			
			// NEW 클래스 명 초기화 
			this.newClassName = null;
		}
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {
		
		super.visitLineNumber(line, start);
		
		// 라인 설정 
		this.line = line;
	}
}
