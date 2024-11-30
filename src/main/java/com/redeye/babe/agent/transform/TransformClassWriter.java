package com.redeye.babe.agent.transform;

import java.util.Hashtable;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * 메소드 변환을 위한 변환 클래스 방문용(ClassVisitor) 클래스
 * @author jmsohn
 */
public class TransformClassWriter extends ClassVisitor {
	
	/** 현재 클래스 명 */
	private String className;
	/** 메소드 변환 맵 */
	private Hashtable<String, TransformMap> transformMaps;

	/**
	 * 생성자
	 * @param api
	 * @param classVisitor
	 */
	public TransformClassWriter(final int api, ClassVisitor classVisitor
			, final Hashtable<String, TransformMap> transformMaps, final String className) {
		
		super(api, classVisitor);
		this.transformMaps = transformMaps;
		this.className = className;
		
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc
			, String signature, String[] exceptions) {

        MethodVisitor mv= super.visitMethod(access, name, desc, signature, exceptions);
        TransformMethodWriter transformMethodWriter = new TransformMethodWriter(this.api, mv, className, name, this.getTransformMaps());
        
        return transformMethodWriter;
    }
	
	/**
	 * 메소드 변환 맵
	 * @return 메소드 변환 맵
	 */
	private Hashtable<String, TransformMap> getTransformMaps() {
		if(this.transformMaps == null) {
			this.transformMaps = new Hashtable<String, TransformMap>();
		}
		
		return this.transformMaps;
	}
	
}
