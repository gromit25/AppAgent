package com.redeye.appagent.agent.transform;

import java.util.Map;
import java.util.Stack;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.redeye.appagent.log.Log;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

/**
 * 메소드를 방문하여, 메소드 내의 API 호출을<br>
 * 변환맵(TransfromMap) 설정에 따라 변환을 수행하는 클래스<br>
 * TransformClassWriter.visitMethod 에서 생성하여 사용함
 * 
 * @author jmsohn
 */
class TransformMethodWriter extends MethodVisitor {
	
	/** 현재 방문한 클래스 명 */
	private String className;
	
	/** 현재 방문한 메소드 명 */
	private String methodName;
	
	/** 현재 명령어의 라인 수 */
	private int lineNumber;
	
	/** API 변환맵 */
	@Getter(AccessLevel.PRIVATE)
	private Map<String, TransformMap> transformMaps;
	
	/** new 명령어 변환용 스택 */
	@Getter(AccessLevel.PRIVATE)
	private Stack<NewInst> newInstStack;
	
	
	/**
	 * 생성자
	 * 
	 * @param api API 버전
	 * @param mv 기본 메소드 Visitor
	 * @param className 현재 방문한 클래스 명
	 * @param methodName 현재 방문한 메소드 명
	 * @param transformMaps API 변환맵
	 */
	TransformMethodWriter(int api, MethodVisitor mv, String className
			, String methodName, Map<String, TransformMap> transformMaps) {
		
		super(api, mv);
		this.className = className;
		this.methodName = methodName;
		this.transformMaps = transformMaps;
		this.newInstStack = new Stack<NewInst>();
		this.lineNumber = -1;
	}
	
	/**
	 * 객체 생성을 위한 new 변환 작업 수행
	 */
	@Override
	public void visitTypeInsn(int opcode, String type) {
		
		TransformMap transformMap = this.getTransformMaps().get(type);
		
		if(opcode == Opcodes.NEW && transformMap != null
			&& transformMap.isTransform(this.className, this.methodName, this.lineNumber)) {
			
			// TODO
			String logMsg = "REPLACE NEW:" + type + " in " + this.className + "." + this.methodName;
			Log.writeAgentLog(logMsg);
			
			// 3바이트 짜리 NEW 명령어를 NOP 3개 명령문으로 치환
			mv.visitInsn(Opcodes.NOP);
			mv.visitInsn(Opcodes.NOP);
			mv.visitInsn(Opcodes.NOP);
			
			// New 변환 상태 스택에 추가
			// 추후 생성자 메소드(<init>) 호출에서 사용하기 위한 용도
			this.getNewInstStack().push(
				new NewInst(type, TransformNewStatus.NEW_INST)
			);
			
		} else {
			super.visitTypeInsn(opcode, type);
		}
		
	}
	
	/**
	 * 
	 */
	@Override
	public void visitInsn(int opcode) {
		
		// 변환 여부 - 변환 작업이 있었는지 여부
		boolean isTransformed = false;
		
		if(opcode == Opcodes.DUP && this.getNewInstStack().isEmpty() == false) {
			
			NewInst newInfo = this.getNewInstStack().pop();
			
			if(newInfo != null && newInfo.getStatus() == TransformNewStatus.NEW_INST) {
			
				// DUP 명령어 변환을 수행하고,
				// 현재 상태를 변경한다.
				
				// DUP 명령어를 NOP 명령문으로 치환한다.
				// TransformMathodWriter.visitInsn 참조
				mv.visitInsn(Opcodes.NOP);
				newInfo.setStatus(TransformNewStatus.NEWDUP_INST);
				
				// 변환 작업을 수행한 것으로 설정
				isTransformed = true;
			}
			
			// 변환된 내용을 스택에 추가
			this.getNewInstStack().push(newInfo);
			
		}
		
		// 변환이 없었을 경우, MethodVisitor의 visitInsn를 호출하여 변환하지 않음
		if(isTransformed == false) {
			super.visitInsn(opcode);
		}
	}
	
	@Override
	public void visitMethodInsn(
			int opcode, String className, String methodName
			, String signature, boolean isInterface
	) {
		
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
					&& this.getNewInstStack().isEmpty() == false) {
				
				// 생성자 호출시에만 변환 수행
				// new 명령어 스택에서 최상단의 하나 추출
				NewInst newInst = this.getNewInstStack().pop();
				
				if(newInst != null && newInst.getStatus() == TransformNewStatus.NEWDUP_INST) {
					
					invokeType.transform(transformMap, this);
					isTransformed = true;
					// new 명령어 변환 스택에 다시 추가하지 않음
					
				} else {
					
					// 변환작업을 수행하지 않으면 다시 new 명령어 스택에 추가
					this.getNewInstStack().push(newInst);
				}
			}
			
		}
		
		// 변환을 수행하지 않았을 경우, MethodVisitor의 visitMethodInst를 호출하여 변환하지 않음
		if(isTransformed == false) {	
			super.visitMethodInsn(opcode, className, methodName, signature, isInterface);
		}
	}
	
	@Override
	public void visitLineNumber(int line, Label start) {
		// 현재 방문한 Line Number 저장 
		this.lineNumber = line;
		super.visitLineNumber(line, start);
	}
}

/**
 * 
 * 
 * @author jmsohn
 */
@Data
class NewInst {
	
	/** 변환 대상 클래스 NEW 명령 변환을 위한 상태 값 */
	private TransformNewStatus status;
	
	/** NEW를 수행한 변환 대상 클래스의 이름 */
	private String targetClassName;
	
	/**
	 * 생성자
	 * 
	 * @param targetClassName
	 * @param status
	 */
	NewInst(String targetClassName, TransformNewStatus status) {
		this.targetClassName = targetClassName;
		this.status = status;
	}
}

/**
 * 
 * 
 * @author jmsohn
 */
enum TransformNewStatus {
	NONE, NEW_INST, NEWDUP_INST
}
