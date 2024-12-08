package com.redeye.babe.agent.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 문자열 처리 유틸리티 클래스
 *
 * @author jmsohn
 */
public class StringUtil {
	
	/**
	 * target 문자열 내에 패턴(pattern)이 마지막 나타난 곳까지의 문자열과<br>
	 * 패턴이 나타난 이후의 문자열을 분리하여 반환<br>
	 * 만일, target 문자열 내에 패턴이 나타나지 않으면 target 문자열만 반환함<br>
	 * <pre>
	 * ex) target: "test1 > test2> test3",
	 *     pattern: "\\s>\\s" 이면
	 *     결과: {"test1 > test2", "test3"}
	 *     
	 *     taget: "test1 > test2"
	 *     pattern: "\\sA\\s" 이면
	 *     결과: {"test1 > test2"}
	 * </pre>
	 * 
	 * @param target 대상 문자열
	 * @param pattern 나눌 패턴
	 * @return 분리된 문자열 배열
	 */
	public static String[] splitLast(String target, String pattern) throws Exception {
		
		// 입력값 검증
		if(target == null) {
			throw new NullPointerException("target is null.");
		}
		
		if(pattern == null) {
			throw new NullPointerException("pattern is null");
		}
		
		// target 문자열에 패턴 적용
		Pattern patternP = Pattern.compile(pattern);
		Matcher patternM = patternP.matcher(target);
		
		int start = -1;
		int end = -1;
		
		// 패턴을 못찾을 때까지, start/end를 업데이트
		while(patternM.find() == true) {
			
			// 패턴이 target 문자열 내에 있는 경우
			// target 문자열에서 처음부터 패턴이 나타나는 곳까지 문자열과
			// 패턴이 나타난 이후 부터 문자열의 끝까지 나눈 문자열을 반환함
			start = patternM.start();
			end = patternM.end();
		}
		
		if(start == -1 || end == -1) { // 못찾은 경우 원래 문자열 반환
			
			return new String[] {
					target
				};
			
		} else { // 한번이라도 찾으면 분리하여 반환
			
			return new String[] {
					target.substring(0, start),
					target.substring(end, target.length())
				};

		}
		
	} // End of splitLast
}
