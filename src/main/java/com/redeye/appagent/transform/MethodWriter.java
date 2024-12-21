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
	@Getter
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
		
		// 로그를 남김
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
		
		// 변환 대상 메소드 명 및 변환 맵을 가져옴
		String targetFullName = TransformMap.makeFullName(className, methodName, signature);
		TransformMap transformMap = this.getTransformMaps().getMap(targetFullName);
		
		// 변환할 메소드인지 검사
		if(transformMap == null) {
			super.visitMethodInsn(opcode, className, methodName, signature, isInterface);
			return;
		}
		
		//---------------------------------------------------
		// 변환 작업 수행
		// - 변환 내용 로깅 
		String logMsg = "REPLACE " + TransformMethodWriter.getInvokeName(opcode) + ":"
				+ className + "." + methodName
				+ " in " + this.className + "." + this.methodName;
		Log.writeAgentLog(logMsg);
		
		if(opcode == Opcodes.INVOKESTATIC || opcode == Opcodes.INVOKEVIRTUAL) {
			
			// INVOKESTATIC/INVOKEVIRTUAL -> static 메소드로 변환 수행
			super.visitMethodInsn(Opcodes.INVOKESTATIC
					, transformMap.getAltClass(), transformMap.getAltMethod()
					, transformMap.getAltSignature(), false);
		
		} else if(opcode == Opcodes.INVOKEINTERFACE) {
			
			// INVOKEINTERFACE -> static 메소드로 변환 수행
			super.visitMethodInsn(Opcodes.INVOKESTATIC
					, transformMap.getAltClass(), transformMap.getAltMethod()
					, transformMap.getAltSignature(), false);
			
			// INVOKEINTERFACE 명령어가 INVOKESTATIC보다 2바이트 크기 때문에,
			// 2바이트 만큼 NOP를 추가하여, while문이나 for문등의 메모리 번지에 영향을 주지 않도록 함
			super.visitInsn(Opcodes.NOP);
			super.visitInsn(Opcodes.NOP);
			
		} else if(opcode == Opcodes.INVOKESPECIAL && this.getTransformNewStack().isEmpty() == false) {
			
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
	
	/**
	 * 함수 호출 코드에 대해 호출 종류를 문자열로 변환하여 반환
	 * 
	 * @param opcode 함수 호출 코드
	 * @return 변환된 문자열
	 */
	private static String getInvokeName(int opcode) {
		
		String invokeName = "N/A(" + opcode + ")";
		
		switch(opcode) {
		
		case Opcodes.INVOKESTATIC:
			invokeName = "INVOKESTATIC";
			break;
			
		case Opcodes.INVOKEVIRTUAL:
			invokeName = "INVOKEVIRTUAL";
			break;
			
		case Opcodes.INVOKEINTERFACE:
			invokeName = "INVOKEINTERFACE";
			break;
			
		case Opcodes.INVOKESPECIAL:
			invokeName = "INVOKESPECIAL";
			break;
			
		case Opcodes.INVOKEDYNAMIC:
			invokeName = "INVOKEDYNAMIC";
			break;
		}
		
		return invokeName;
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {
		
		super.visitLineNumber(line, start);
		
		// 라인 설정 
		this.line = line;
	}
}
