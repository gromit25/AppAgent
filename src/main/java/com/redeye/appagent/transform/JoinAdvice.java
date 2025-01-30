package com.redeye.appagent.transform;

import java.util.ArrayList;
import java.util.List;

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
	 * 주어진 어드바이스 문자열로 어드바이스 객체 생성 및 반환
	 * 
	 * @param adviceStrs 어드바이스 문자열
	 * @return 생성된 어드바이스 객체
	 */
	public static List<JoinAdvice> create(String adviceStrs) throws Exception {
		
		// 어드바이스 목록
		List<JoinAdvice> advices = new ArrayList<>();
		
		// 어드바이스 문자열을 분리(,) 하여
		// 각 어드바이스 객체를 생성하여 목록에 저장
		for(String adviceStr: adviceStrs.split(",")) {
		
			// 클래스 명 과 메소드 명 어드바이스 분리
			String[] classAndMethodAdvice = StringUtil.splitLast(adviceStr, ".");
			if(classAndMethodAdvice.length != 2) {
				throw new Exception("advice is invalid:" + adviceStr);
			}
			
			// 어드바이스 객체 생성
			JoinAdvice advice = new JoinAdvice();
			
			advice.classAdvice = StringUtil.WildcardPattern.create(classAndMethodAdvice[0]);
			advice.methodAdvice = StringUtil.WildcardPattern.create(classAndMethodAdvice[1]);
			
			// 어드바이스 목록에 추가
			advices.add(advice);
		}
		
		return advices;
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
