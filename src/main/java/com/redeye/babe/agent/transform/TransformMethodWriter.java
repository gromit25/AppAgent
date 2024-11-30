package com.redeye.babe.agent.transform;

import java.util.Hashtable;
import java.util.Stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.babe.log.Log;

/**
 * 메소드를 방문하여, 메소드 내의 API 호출을
 * 변환맵(TransfromMap) 설정에 따라 변환을 수행하는 클래스
 * TransformClassWriter.visitMethod 에서 생성하여 사용함
 * @author jmsohn
 */
class TransformMethodWriter extends MethodVisitor {
	
	/** 현재 방문한 클래스 명 */
	private String className;
	/** 현재 방문한 메소드 명 */
	private String methodName;
	/** */
	private int lineNumber;
	/** API 변환맵 */
	private Hashtable<String, TransformMap> transformMaps;
	/** */
	private Stack<TransformNewInfo> transformNewStack;
	
	/**
	 * 생성자
	 * @param api API 버전
	 * @param mv 기본 메소드 Visitor
	 * @param className 현재 방문한 클래스 명
	 * @param methodName 현재 방문한 메소드 명
	 * @param transformMaps API 변환맵
	 */
	TransformMethodWriter(int api, MethodVisitor mv, String className
			, String methodName, Hashtable<String, TransformMap> transformMaps) {
		
		super(api, mv);
		this.className = className;
		this.methodName = methodName;
		this.transformMaps = transformMaps;
		// 최초에 한번 클리어 작업을 한다. -> 혹시 몰라서
		this.transformNewStack = new Stack<TransformNewInfo>();
		this.lineNumber = -1;
		
	}
	
	@Override
	public void visitTypeInsn(int opcode, String type) {
		
		TransformMap transformMap = this.getTransformMaps().get(type);
		
		if(opcode == Opcodes.NEW && transformMap != null
			&& transformMap.isTransform(this.className, this.methodName, this.lineNumber)) {
			
			//TODO
			String logMsg = "REPLACE NEW:" + type + " in " + this.className + "." + this.methodName;
			Log.writeAgentLog(logMsg);
			
			// 3바이트 짜리 NEW 명령어를 NOP 3개 명령문으로 치환한다.
			// while문이나 for문등의 메모리 번지에 영향을 주지 않도록 한다.
			mv.visitInsn(Opcodes.NOP);
			mv.visitInsn(Opcodes.NOP);
			mv.visitInsn(Opcodes.NOP);
			
			// New 변환 상태 스택 최상단에 추가한다.
			this.getTransformNewStack().push(new TransformNewInfo(type, TransformNewStatus.NEW_INST));
			
		} else {
			super.visitTypeInsn(opcode, type);
		}
		
	}
	
	@Override
	public void visitInsn(int opcode) {
		
		boolean isTransformed = false;
		
		if(opcode == Opcodes.DUP && this.getTransformNewStack().isEmpty() == false) {
			
			TransformNewInfo newInfo = this.getTransformNewStack().pop();
			
			if(newInfo != null && newInfo.getStatus() == TransformNewStatus.NEW_INST) {
			
				// DUP 명령어 변환을 수행하고,
				// 현재 상태를 변경한다.
				
				// DUP 명령어를 NOP 명령문으로 치환한다.
				// TransformMathodWriter.visitInsn 참조
				mv.visitInsn(Opcodes.NOP);
				newInfo.setStatus(TransformNewStatus.NEWDUP_INST);
				
				isTransformed = true;
			}
			
			// 다시 넣어 둔다.
			this.getTransformNewStack().push(newInfo);
			
		}
		
		if(isTransformed == false) {
			super.visitInsn(opcode);
		}
	}
	
	@Override
	public void visitMethodInsn(int opcode, String className, String methodName
			, String signature, boolean isInterface) {
		
		//
		InvokeType invokeType = InvokeType.getInvokeType(opcode);
		String targetFullName = TransformMap.makeFullName(className, methodName, signature);
		TransformMap transformMap = this.getTransformMaps().get(targetFullName);
		
		boolean isTransformed = false;
		
		if(invokeType != null && transformMap != null
			&& transformMap.isTransform(this.className, this.methodName, this.lineNumber)) {
			
			// 변환 작업을 수행한다.
			// - 변환 내용 로깅 
			String logMsg = "REPLACE " + invokeType.name() + ":"
					+ className + "." + methodName
					+ " in " + this.className + "." + this.methodName;
			Log.writeAgentLog(logMsg);
			
			// - mapping된 변환 메소드 호출로 변경
			if(invokeType != InvokeType.INVOKESPECIAL) {
				
				// INVOKESTATIC, INVOKEVIRTUAL, INVOKEINTERFACE
				invokeType.transform(transformMap, this);
				
				isTransformed = true;
				
			} else if(invokeType == InvokeType.INVOKESPECIAL
					&& this.getTransformNewStack().isEmpty() == false) {
				
				// 생성자 호출시에만 변환을 수행한다.
				// new 명령어 변환 스택에서 최상단의 하나를 추출한다.
				TransformNewInfo newInfo = this.getTransformNewStack().pop();
				
				if(newInfo != null && newInfo.getStatus() == TransformNewStatus.NEWDUP_INST) {
					
					invokeType.transform(transformMap, this);
					isTransformed = true;
					// new 명령어 변환 스택에 다시 안 넣는다.
					
				} else {
					
					// 변환작업을 수행하지 않아, 다시 new 명령어 변환 스택에 다시 넣는다.
					this.getTransformNewStack().push(newInfo);
				}
			}
			
		}
		
		if(isTransformed == false) {	
			// 변환을 수행하지 않았을 경우 그냥 기존 그대로 놔둔다.
			super.visitMethodInsn(opcode, className, methodName, signature, isInterface);
		}
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {
		// 현재 방문한 Line Number를 저장한다. 
		this.lineNumber = line;
		super.visitLineNumber(line, start);
	}

	/**
	 * 
	 * @return
	 */
	private Hashtable<String, TransformMap> getTransformMaps() {
		
		if(this.transformMaps == null) {
			this.transformMaps = new Hashtable<String, TransformMap>();
		}
		
		return this.transformMaps;
	}
	
	public String getMethodName() {
		return this.methodName;
	}

	private Stack<TransformNewInfo> getTransformNewStack() {
		
		if(this.transformNewStack == null) {
			this.transformNewStack = new Stack<TransformNewInfo>();
		}
		
		return this.transformNewStack;
	}

}

/**
 * 
 * @author jmsohn
 */
class TransformNewInfo {
	
	/** 변환 대상 클래스 NEW 명령 변환을 위한 상태 값 */
	private TransformNewStatus status;
	/** NEW를 수행한 변환 대상 클래스의 이름 */
	private String targetClassName;
	
	TransformNewInfo(String targetClassName, TransformNewStatus status) {
		this.targetClassName = targetClassName;
		this.status = status;
	}
	
	TransformNewStatus getStatus() {
		return this.status;
	}

	void setStatus(TransformNewStatus status) {
		this.status = status;
	}

	String getTargetClassName() {
		return this.targetClassName;
	}

	void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}
}

enum TransformNewStatus {
	NONE, NEW_INST, NEWDUP_INST
}
