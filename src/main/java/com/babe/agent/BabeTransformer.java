package com.babe.agent;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Hashtable;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import com.babe.agent.transform.TransformClassWriter;
import com.babe.agent.transform.TransformMap;
import com.babe.agent.transform.TransformMapConfigReader;
import com.babe.log.Log;

/**
 * API 호출부를 모니터링 메소드 호출로 변경하는 클래스
 * @author jmsohn
 */
public final class BabeTransformer implements ClassFileTransformer {
	
	/** 전체 스킵 여부(테스트용) */
	private boolean isSkip = false;
	
	/** API 호출 변환 맵 */ 
	private Hashtable<String, TransformMap> transformMap;
	private HashSet<String> exceptionalCodeSources;

	/**
	 * 생성자
	 * @param transformConfigFile 클래스 변경 설정 파일
	 * @param exceptionalCodeSources 예외 코드 소스 목록들
	 */
	public BabeTransformer(final File transformConfigFile
			, final HashSet<String> exceptionalCodeSources) throws Exception {
		
		this.setExceptionalCodeSources(exceptionalCodeSources);
		this.transformMap = TransformMapConfigReader.readConfig(transformConfigFile);
		
	}
	
	/**
	 * 로딩되는 클래스 변환 수행
	 */
	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {
		return this.transformAPI(className, protectionDomain, classfileBuffer);
	}
	
	/**
	 * 
	 * @param className
	 * @param protectionDomain
	 * @param classfileBuffer
	 * @return
	 */
	public byte[] transformAPI(String className, ProtectionDomain protectionDomain
		, byte[] classfileBuffer)throws IllegalClassFormatException {
		
		try {

			// 클래스 스킵여부 확인
			if(this.isSkip(className, protectionDomain) == true) {
				return classfileBuffer;
			}
			
			// 클래스 변환을 수행한다.
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			TransformClassWriter transformWriter = new TransformClassWriter(Opcodes.ASM5, cw, this.getTransformMap(), className);
			
			ClassReader cr = new ClassReader(classfileBuffer);
			cr.accept(transformWriter, 0);
			
			// 변환된 결과를 리턴한다.
			return cw.toByteArray();

		} catch(Exception ex) {
			
			// 예외발생시, 로깅 수행
			StringBuilder msgBuilder = new StringBuilder("");
			msgBuilder.append("*** Transform Exception occured at " + className);
			if(protectionDomain != null) {
				msgBuilder.append(" in " + protectionDomain.getCodeSource().getLocation().toString());
			}
			
			Log.writeAgentLog(msgBuilder.toString());
			ex.printStackTrace();
			
			return classfileBuffer;
		}
	}
	
	/**
	 * 현재 클래스의 변경여부를 결정
	 * @param className
	 * @param protectionDomain
	 * @return 스킵여부(스킵시 true, 변경(transform)시 false)
	 */
	private boolean isSkip(final String className, final ProtectionDomain protectionDomain) {
		
		//---------------------
		// 스킵 설정이 되어있으면 전체 스킵함
		if(this.isSkip == true) {
			return true;
		}
		
		// TODO
		// 나중에 삭제 해야함.
		if(className == null || className.startsWith("com/babe") == true) {
			return true;
		}
		
		//---------------------
		// protection domain이 null인 것은 boot library 임
		// 좀더 확인 필요... 확신을 몬하겠음
		// TODO
		if(protectionDomain == null || protectionDomain.getCodeSource() == null ) {
			return true;
		}
		
		//---------------------
		// 로딩된 위치가 hash 검증이 되었으면,
		// skip 한다.
		String codeSourceLoc = protectionDomain.getCodeSource().getLocation().toString();
		if(this.getExceptionalCodeSources().contains(codeSourceLoc) == true) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * 변환 규칙(transferConfigFile에 있는 내용)을 가져온다. 
	 * ex) java/lang/Runtime.exec(Ljava/lang/String;)Ljava/lang/Process;
	 *     -> com/dave/wrapper/RuntimeWrapper.exec(Ljava/lang/Runtime;Ljava/lang/String;)Ljava/lang/Process;
	 * @return
	 */
	private Hashtable<String, TransformMap> getTransformMap() {
		if(this.transformMap == null) {
			this.transformMap = new Hashtable<String, TransformMap>();
		}
		return this.transformMap;
	}

	/**
	 * 
	 * @return
	 */
	private HashSet<String> getExceptionalCodeSources() {
		if(this.exceptionalCodeSources == null) {
			this.exceptionalCodeSources = new HashSet<String>();
		}
		return this.exceptionalCodeSources;
	}

	/**
	 * 
	 * @param exceptionalCodeSources
	 */
	private void setExceptionalCodeSources(final HashSet<String> exceptionalCodeSources) {
		this.exceptionalCodeSources = exceptionalCodeSources;
	}
}
