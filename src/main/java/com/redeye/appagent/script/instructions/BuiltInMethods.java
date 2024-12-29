package com.redeye.appagent.script.instructions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.redeye.appagent.util.StringUtil.WildcardPattern;

/**
 * built in method 클래스
 * 
 * @author jmsohn
 */
public class BuiltInMethods {
	
	/**
	 * double -> int 로 변환
	 * 
	 * @param value 변환할 값
	 * @return 변환된 int
	 */
	@MethodAlias(alias = "toInt")
	public static int toInt(double value) {
		return (int)value;
	}
	
	/**
	 * 반올림 메소드
	 * 
	 * @param value 반올림 대상
	 * @return 반올림 수행 결과
	 */
	@MethodAlias(alias = "round")
	public static double round(double value) {
		return Math.round(value);
	}
	
	/**
	 * 지수 곱 메소드
	 * 
	 * @param base 지수 곱을 할 base 값
	 * @param exponent 지수 곱 횟수
	 * @return 지수 곱 결과
	 */
	@MethodAlias(alias = "pow")
	public static double pow(double base, double exponent) {
		return Math.pow(base, exponent);
	}
	
	/**
	 * 와일드 카드 매치 메소드
	 * 
	 * @param pattern 와일드 카드 패턴
	 * @param target 매치할 문자열
	 * @return 와일드 카드 매치 여부
	 */
	@MethodAlias(alias = "matchW")
	public static boolean matchWildcard(String pattern, String target) throws Exception {
		return WildcardPattern.create(pattern).match(target).isMatch();
	}
	
	/**
	 * 정규 표현식 매치 메소드
	 * 
	 * @param pattern 정규 표현식
	 * @param target 매치할 문자열
	 * @return 정규 표현식 매치 여부
	 */
	@MethodAlias(alias = "matchR")
	public static boolean matchRegExp(String pattern, String target) throws Exception {
		return target.matches(pattern);
	}
	
	/**
	 * if 메소드
	 * 
	 * @param condition if 조건
	 * @param trueValue if 조건이 true 일때, 반환할 값
	 * @param falseValue if 조건이 false 일때, 반환할 값
	 * @return if 조건에 따라 반환 값(trueValue or falseValue)
	 */
	@MethodAlias(alias = "if")
	public static Object ifMethod(Boolean condition, Object trueValue, Object falseValue) throws Exception {
		
		if(condition == true) {
			return trueValue;
		} else {
			return falseValue;
		}
	}
	
	/**
	 * 현재 시간을 포맷에 맞추어 문자열로 반환
	 * 
	 * @param format 시간 출력 포맷
	 * @return 현재 시간 문자열
	 */
	@MethodAlias(alias = "now")
	public static String now(String format) throws Exception {
		
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		
		return formatter.format(now);
	}
}
