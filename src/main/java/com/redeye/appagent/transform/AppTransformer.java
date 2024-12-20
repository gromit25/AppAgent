package com.redeye.appagent.transform;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Hashtable;
import java.util.Map;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NOP;

import com.redeye.appagent.Config;
import com.redeye.appagent.logger.Log;
import com.redeye.appagent.transform.TransformClassWriter;
import com.redeye.appagent.transform.TransformMap;
import com.redeye.appagent.transform.TransformMapConfigReader;

/**
 * API 호출부를 모니터링 메소드 호출로 변환하는 클래스
 * 
 * @author jmsohn
 */
public final class AppTransformer implements ClassFileTransformer {
	
	/** 전체 스킵 여부(테스트용) */
	private boolean isSkip = false;
	
	/** API 호출 변환 맵 */ 
	private Map<String, TransformMap> transformMap;
	
	/**
	 * 생성자
	 * 
	 * @param configFile 클래스 변환 설정 파일 위치 문자열
	 */
	public AppTransformer(String configFile) throws Exception {
		this(new File(configFile));
	}
	
	/**
	 * 생성자
	 * 
	 * @param configFile 클래스 변환 설정 파일
	 */
	public AppTransformer(File configFile) throws Exception {

		// 클래스 변환 설정을 읽어옴
		this.transformMap = TransformMapConfigReader.readConfig(configFile);
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
				
				String invokeClassName = invoke.getClassName(cpg);
				String invokeMethodName = invoke.getMethodName(cpg);
				
				if(
					invokeClassName.equals("java.sql.PreparedStatement") == true
					&& invokeMethodName.equals("executeQuery") == true
				) {
					
					int methodRef = cpg.addMethodref("com.redeye.appagent.wrapper.StatementTestWrapper", "executeQuery", "(Ljava/sql/PreparedStatement;)Ljava/sql/ResultSet;");
					
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
