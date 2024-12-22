package com.redeye.appagent.transform;

import java.util.Stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.redeye.appagent.logger.Log;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 
 * @author jmsohn
 */
public class MethodWriter extends MethodVisitor {
	
	/** 클래스 명 */
	@Getter
	private String className;
	
	/** 메소드 명 */
	@Getter
	private String methodName;
	
	/** 소스 라인 번호 */
	@Getter
	private int line;
	
	/** */
	private Stack<String> newClsStack;
	
	/**
	 * 생성자
	 * 
	 * @param api API 버전
	 * @param mv 기본 메소드 Visitor
	 * @param className 현재 방문한 클래스 명
	 * @param methodName 현재 방문한 메소드 명
	 */
	MethodWriter(int api, MethodVisitor mv, String className, String methodName) {
		
		super(api, mv);
		
		this.className = className;
		this.methodName = methodName;
		this.line = -1;
		
		this.newClsStack = new Stack<>();
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type) {
		
		//
		if(opcode != Opcodes.NEW
			|| this.getTransformMaps().containsInNewClasses(type) == false
			|| isJoinClass(type) == false
		) {
			
			super.visitTypeInsn(opcode, type);
			return;
		}
		
		// 로깅
		String logMsg = "REPLACE NEW:" + type + " in " + this.className + "." + this.methodName;
		Log.writeAgentLog(logMsg);
		
		// 3바이트 NEW 명령어 -> NOP 3개 명령문 치환
		super.visitInsn(Opcodes.NOP);
		super.visitInsn(Opcodes.NOP);
		super.visitInsn(Opcodes.NOP);
		
		// NEW 클래스 목록 추가
		this.newClsStack.push(type);
	}
	
	@Override
	public void visitInsn(int opcode) {
		
		if(opcode == Opcodes.DUP && this.newClsStack.isEmpty() == false) {
			
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
		
		// 변환 대상 메소드 스펙 획득
		MethodSpec targetSpec = MethodSpec.create(className, methodName, signature);
		MethodSpec altSpec = MethodMap.getAltMethod(targetSpec);
		
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
			+ targetSpec.toString()
			+ " in "
			+ this.className + "." + this.methodName;
		Log.writeAgentLog(logMsg);
		
		if(opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.INVOKEVIRTUAL) {
			
			// INVOKESTATIC/INVOKEVIRTUAL 변환
			super.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				altSpec.getClassName(),
				altSpec.getMethodName(),
				altSpec.getSignature(),
				false
			);
		
		} else if(opcode == Opcodes.INVOKEINTERFACE) {
			
			// INVOKEINTERFACE
			super.visitMethodInsn(
				Opcodes.INVOKESTATIC,
				altSpec.getClassName(),
				altSpec.getMethodName(),
				altSpec.getSignature(),
				false
			);
			
			super.visitInsn(Opcodes.NOP);
			super.visitInsn(Opcodes.NOP);
			
		} else if(opcode == Opcodes.INVOKESPECIAL && this.newClsStack.isEmpty() == false) {
			
			// 생성자 호출시에만 변환을 수행
			// new 명령어 변환 스택에서 최상단의 하나를 추출
			TransformNewInfo newInfo = this.getTransformNewStack().pop();
			
			if(newInfo != null && newInfo.getStatus() == TransformNewStatus.NEWDUP_INST) {
				
				// INVOKESTATIC으로 변환
				super.visitMethodInsn(Opcodes.INVOKESTATIC
						, transformMap.getAltClass(), transformMap.getAltMethod()
						, transformMap.getAltSignature(), false);
				
			} else {
				
				// 변환작업을 수행하지 않아, 다시 new 명령어 변환 스택에 다시 넣음
				this.getTransformNewStack().push(newInfo);
			}
			
		} else {
			
			//
			super.visitMethodInsn(opcode, className, methodName, signature, isInterface);
			return;
		}
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {
		
		super.visitLineNumber(line, start);
		
		// 라인 설정 
		this.line = line;
	}
}
