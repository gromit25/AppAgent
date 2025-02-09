package com.redeye.appagent.rewriter;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import lombok.Getter;

/**
 * 클래스 변환<br>
 * 메소드 변환 클래스 호출 용도
 * 
 * @author jmsohn
 */
public class AppClassWriter extends ClassVisitor {
	
	/** 클래스 명 */
	@Getter
	private String className;
	
	/**
	 * 생성자
	 * 
	 * @param api
	 * @param classVisitor
	 * @param className
	 */
	public AppClassWriter(int api, ClassVisitor classVisitor, String className) {
		
		super(api, classVisitor);
		
		this.className = className;
	}

	@Override
	public MethodVisitor visitMethod(
		int access,
		String methodName,
		String desc,
		String signature,
		String[] exceptions
	) {
		
        MethodVisitor mv = super.visitMethod(access, methodName, desc, signature, exceptions);
        AppMethodWriter methodWriter = new AppMethodWriter(this.api, mv, className, methodName);
        
        return methodWriter;
    }
}
