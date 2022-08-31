package com.babe.agent.transform;

import java.util.Hashtable;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * 메소드 호출 타입
 * @author jmsohn
 */
enum InvokeType {
	
	INVOKEVIRTUAL(Opcodes.INVOKEVIRTUAL),
	INVOKESTATIC(Opcodes.INVOKESTATIC),
	INVOKEINTERFACE(Opcodes.INVOKEINTERFACE) {
		@Override
		public void transform(TransformMap transformMap, MethodVisitor mv) {
			super.transform(transformMap, mv);
			
			// INVOKEINTERFACE 명령어가 INVOKESTATIC보다 2바이트 크기 때문에,
			// 2바이트 만큼 NOP를 추가하여, while문이나 for문등의 메모리 번지에 영향을 주지
			// 않도록 한다.
			mv.visitInsn(Opcodes.NOP);
			mv.visitInsn(Opcodes.NOP);
		}
	},
	INVOKESPECIAL(Opcodes.INVOKESPECIAL);
	
	// -------------
	/**	invoke type 목록 */
	private static Hashtable<Integer, InvokeType> invokeTypes;
	
	/**
	 * 
	 */
	static {
		InvokeType.invokeTypes = new Hashtable<Integer, InvokeType>();
		
		for(InvokeType invokeType : InvokeType.values()) {
			InvokeType.invokeTypes.put(invokeType.getOpcode(), invokeType);
		}
	}
	
	/**
	 * 
	 * @param opcode
	 * @return
	 */
	public static InvokeType getInvokeType(int opcode) {
		if(InvokeType.invokeTypes != null) {
			return InvokeType.invokeTypes.get(opcode);
		} else {
			return null;
		}
	}

	// --------------
	/** opcode */
	private int opcode;
	
	/**
	 * invoke 변환 수행
	 * @param transformMap 변환 맵
	 * @param mv Method Visitor
	 */
	public void transform(TransformMap transformMap, MethodVisitor mv) {
		if(transformMap == null || mv == null) return;
		
		// static 메소드로 변환 수행
		mv.visitMethodInsn(Opcodes.INVOKESTATIC
				, transformMap.getAltAPIClass(), transformMap.getAltAPIMethod()
				, transformMap.getAltAPISignature(), false);
	}
	
	/**
	 * 
	 * @param opcode
	 */
	InvokeType(int opcode) {
		this.opcode = opcode;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getOpcode() {
		return this.opcode;
	}
	
}
