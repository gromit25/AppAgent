package com.redeye.appagent.appwriter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.redeye.appagent.Config;
import com.redeye.appagent.util.StringUtil;

/**
 * 조인 어드바이스
 * 
 * @author jmsohn
 */
class JoinAdvice {

	/** 조인할 클래스 명 어드바이스 */
	private StringUtil.WildcardPattern classAdvice;
	
	/** 조인할 메소드 명 어드바이스 */
	private StringUtil.WildcardPattern methodAdvice;
	
	/**
	 * 생성자
	 */
	private JoinAdvice() throws Exception {
	}
	
	/**
	 * 메소드의 어드바이스 어노테이션에 설정된 문자열로 어드바이스 객체 생성 및 반환
	 * 
	 * @param method 대상 메소드
	 * @return 생성된 어드바이스 객체
	 */
	public static List<JoinAdvice> create(Method method) throws Exception {
		
		// method가 null 일 경우 빈 목록 반환
		if(method == null) {
			return new ArrayList<>();
		}
		
		// 조인 어드바이스 객체 생성, 없을 경우 환경 변수의 조인 어드바이스를 사용
		com.redeye.appagent.annotation.JoinAdvice adviceAnnotation =
				method.getDeclaredAnnotation(com.redeye.appagent.annotation.JoinAdvice.class);

		String[] adviceStrs = new String[] {Config.GLOBAL_JOIN_ADVICE.getValue()};
		if(adviceAnnotation != null) {
			adviceStrs = adviceAnnotation.value();
		}
		
		// 메소드에 설정된 어드바이스 문자열로 생성
		return JoinAdvice.create(adviceStrs);
	}
	
	/**
	 * 주어진 어드바이스 문자열로 어드바이스 객체 생성 및 반환
	 * 
	 * @param adviceStrs 어드바이스 문자열
	 * @return 생성된 어드바이스 객체
	 */
	public static List<JoinAdvice> create(String[] adviceStrArr) throws Exception {
		
		// 어드바이스 목록
		List<JoinAdvice> adviceList = new ArrayList<>();
		
		// 어드바이스 문자열이 없을 경우 목록 추가 없이 반환
		if(adviceStrArr == null || adviceStrArr.length == 0) {
			return advices;
		}
		
		// 어드바이스 문자열 별로
		// 각 어드바이스 객체를 생성하여 목록에 저장
		for(String adviceStr: adviceStrArr) {
		
			// 클래스 명 과 메소드 명 어드바이스 분리
			String[] classAndMethodAdvice = StringUtil.splitLast(adviceStr, "\\.");
			
			if(
				classAndMethodAdvice == null
				||
				(classAndMethodAdvice.length != 1 && classAndMethodAdvice.length != 2)
			) {
				throw new Exception("advice is invalid:" + adviceStr);
			}
			
			String classAdvice = classAndMethodAdvice[0];
			String methodAdvice = "*";
			if(classAndMethodAdvice.length == 2) {
				methodAdvice = classAndMethodAdvice[1];
			}
			
			// 어드바이스 객체 생성
			JoinAdvice advice = new JoinAdvice();
			
			advice.classAdvice = StringUtil.WildcardPattern.create(classAdvice);
			advice.methodAdvice = StringUtil.WildcardPattern.create(methodAdvice);
			
			// 어드바이스 목록에 추가
			adviceList.add(advice);
		}
		
		// 어드바이스 목록 반환
		return adviceList;
	}
	
	/**
	 * 주어진 클래스 명 및 메소드 명에 대해 조인할 것인지 여부 반환
	 * 
	 * @param className 검사할 클래스 명
	 * @param methodName 검사할 메소드 명
	 * @return 조인 여부
	 */
	public boolean isJoin(String className, String methodName) {
		
		return
			this.classAdvice.match(className).isMatch()
			&& this.methodAdvice.match(methodName).isMatch();
	}
}
