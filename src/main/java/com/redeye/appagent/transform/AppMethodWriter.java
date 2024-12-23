package com.redeye.appagent.transform;

import java.util.Stack;

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
	 * new 명령어 스택<br>
	 * 만일 new 가 중첩되어 있는 경우 -> new A(new B()), A, B 모두 대상일 경우<br>
	 * new 명령어는 중첩된 형태로 명령어가 나타나기 때문에 FIFO 구조인 스택을 이용해 처리함<br>
	 * ex) new A(new B())의 byte code:<br>  
	 * <pre>
	 *   new #19 <com/test/A>
	 *   dup
	 *   new #21 <com/test/B>
	 *   dup
	 *   invokespecial #23 <com/test/B.<init> : ()V>
	 *   invokespecial #24 <com/test/A.<init> : (Lcom/test/B;)V>
	 *   astore_1
	 *   return
	 * </pre>
	 * 
	 */
	private Stack<New> newStack;
	
	/**
	 * new 명령어의 클래스명과 현재 실행 명령어
	 * 
	 * @author jmsohn
	 */
	class New {
		
		/** 클래스 명 */
		String className;
		
		/** 현재 실행 명령어 */
		NewInst inst;
		
		/**
		 * 생성자
		 * 
		 * @param className 클래스 명
		 */
		New(String className) {
			this.className = className;
			this.inst = NewInst.NEW;
		}
	}
	
	/**
	 * new 실행 명령어<br>
	 * DUP 이후에 생성자 메소드가 나타나면
	 * 즉시 스택에서 삭제 되므로 DUP 이후의 실행 상태는 필요 없음<br> 
	 * 순서) NEW 객체 생성 -> DUP -> 매개변수 실행 -> 생성자 메소드(<init>) 호출
	 * 
	 * @author jmsohn
	 */
	enum NewInst {
		NEW,
		DUP;
	}
	
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
		
		this.newStack = new Stack<>();
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
		
		// NEW 클래스 생성 정보 추가
		this.newStack.push(new New(type));
	}
	
	@Override
	public void visitInsn(int opcode) {
		
		// DUP 명령어가 아닐 경우 변환하지 않음
		if(opcode != Opcodes.DUP) {
			super.visitInsn(opcode);
			return;
		}

		// new 스택이 비어 있을 경우 변환하지 않음
		if(this.newStack.isEmpty() == false) {
			super.visitInsn(opcode);
			return;
		}

		// 최근 new 명령어 실행 상태가 NEW가 아닐 경우 변환하지 않음 
		// 이미 DUP 을 변환한 상태임
		New newInfo = this.newStack.pop();
		if(newInfo.inst != NewInst.NEW) {
			
			this.newStack.push(newInfo);
			
			super.visitInsn(opcode);
			return;
		}
		
		// ----- 변환 작업 수행
		
		// DUP 명령어 -> NOP 명령어 치환
		super.visitInsn(Opcodes.NOP);
		
		// DUP 명령어 상태로 변경하여 추가
		newInfo.inst = NewInst.DUP;
		this.newStack.push(newInfo);
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
			
		} else if(opcode == Opcodes.INVOKESPECIAL) {
			
			// New 스택에서 최상단 삭제 
			this.newStack.pop();
		}
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {

		// 라인 설정 
		this.line = line;
		
		super.visitLineNumber(line, start);
	}
}
