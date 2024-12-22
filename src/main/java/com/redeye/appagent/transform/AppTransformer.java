package com.redeye.appagent.transform;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.sql.PreparedStatement;

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
	
	/** 임시 변환 메소드 맵 */
	MethodPair alterMethodMap;
	
	/**
	 * 생성자
	 */
	public AppTransformer() throws Exception {
		
		// TODO 테스트용 임시 초기화
		MethodPair alterMethodMap = new MethodPair();
		
		// 대상 메소드 설정
		alterMethodMap.setTargetMethod(
			"java.sql.PreparedStatement",
			"executeQuery",
			"()Ljava/sql/ResultSet;"
		);
		
		// 변경 대상 메소드 설정
		Class<?> alterClass = PreparedStatementWrapper.class;
		java.lang.reflect.Method alterMethod = alterClass.getDeclaredMethod("executeQuery", PreparedStatement.class);
		alterMethodMap.setAlterMethod(alterMethod);
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
			
			//
			JavaClass clazz = new ClassParser(new ByteArrayInputStream(classfileBuffer), null).parse();
			//
			ClassGen classGen = new ClassGen(clazz);
			
			//
			for(Method method : clazz.getMethods()) {
				transformMethod(classGen, method);
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
		// 스킵 설정이 되어있으면 전체 스킵
		if(this.isSkip == true) {
			return true;
		}
		
		// class 명이 없거나 AppAgent의 클래스이면 스킵
		if(className == null || className.startsWith(Config.AGENT_PACKAGE.getValue()) == true) {
			return true;
		}
		
		//---------------------
		// protection domain이 null인 것은 boot library 이므로 스킵
		// 아닐 경우 스킵하지 않도록 함
		if(protectionDomain == null || protectionDomain.getCodeSource() == null ) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 주어진 Method 의 ByteCode 를 변환
	 * 
	 * @param classGen 변환할 Method 의 클래스 
	 * @param method 변환할 메소드
	 */
	private void transformMethod(
		ClassGen classGen,
		Method method
	) throws Exception {
		
		ConstantPoolGen cpg = classGen.getConstantPool();
		
		MethodGen methodGen = new MethodGen(
			method, classGen.getClassName(), cpg
		);
		
		InstructionList il = methodGen.getInstructionList();
		InstructionHandle ih = il.getStart();

		while(ih != null) {
			
			Instruction inst = ih.getInstruction();
			
			if(inst.getOpcode() == Const.INVOKEINTERFACE) {
				
				INVOKEINTERFACE invoke = (INVOKEINTERFACE)inst;
				MethodSpec invokeMethodSpec = MethodSpec.create(invoke, cpg);
				
				if(this.alterMethodMap.getTargetMethod().equals(invokeMethodSpec) == true) {
					
					int methodRef = alterMethodMap.getAlterMethod().getMethodRef(cpg);
					
					il.append(ih, new NOP());
					il.append(ih, new NOP());
					il.append(ih, new INVOKESTATIC(methodRef));
					il.delete(ih);
				}
			}
			
			// method 내에 다음 명령어 획득
			ih = ih.getNext();
		} // End of while instruction handle
		
        // Set the InstructionList to the MethodGen
        methodGen.setInstructionList(il);

        // Update the constant pool and other method information
        methodGen.setMaxLocals();
        methodGen.setMaxStack();
        
        //methodGen.removeLineNumbers();
        methodGen.removeLocalVariables();
        
        //
        classGen.replaceMethod(method, methodGen.getMethod());
	}
}
