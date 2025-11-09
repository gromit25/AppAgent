package com.redeye.appagent.util;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * 문자열 처리 유틸리티 클래스
 *
 * @author jmsohn
 */
public class StringUtil {
	
	/**
	 * 주어진 문자열에 대한 이스케이프 처리<br>
	 * ex) "ab\\tc" -> "ab\tc"
	 * 
	 * @param str 주어진 문자열
	 * @return 이스케이프 처리된 문자열
	 */
	public static String escape(String str) throws Exception {
		
		// 입력값 검증
		if(str == null) {
			return null;
		}
		
		// 이스케이프 처리된 문자열 변수
		StringBuilder escapedStr = new StringBuilder("");
		
		// 유니코드 임시 저장 변수
		StringBuilder unicodeStr = new StringBuilder(""); 
		
		// 이스케이프 처리를 위한 상태 변수
		// 0: 문자열, 1: 이스케이프 문자,
		// 11:유니코드 1번째 문자, 12:유니코드 2번째 문자, 13:유니코드 3번째 문자, 14:유니코드 4번째 문자 
		int status = 0;
		
		for(int index = 0; index < str.length(); index++) {
			
			char ch = str.charAt(index);
			
			if(status == 0) {
				
				if(ch == '\\') {
					status = 1;
				} else {
					escapedStr.append(ch);
				}
				
			} else if(status == 1) {
				
				// 상태를 일반 문자열 상태로 설정
				// 먼저 상태를 변경하는 이유는 Unicode 시작시 상태가 10으로 변경하기 때문에
				// 마지막에 상태를 변경하면 안됨
				status = 0;
				
				if(ch == '0') {
					escapedStr.append('\0'); // ASCII 0 추가
				} else if(ch == 'b') {
					escapedStr.append('\b');
				} else if(ch == 'f') {
					escapedStr.append('\f');
				} else if(ch == 'n') {
					escapedStr.append('\n');
				} else if(ch == 'r') {
					escapedStr.append('\r');
				} else if(ch == 't') {
					escapedStr.append('\t');
				} else if(ch == 'u') {
					// Unicode 시작
					status = 11;
				} else {
					// 없을 경우 해당 문자를 그냥 추가함
					// ex) \' 인경우 '를 추가
					escapedStr.append(ch);
				}
				
			} else if(status >= 10 && status <= 14) {
				
				// ch가 16진수 값(0-9, A-F, a-f) 인지 확인
				if(isHex(ch) == false) {
					throw new Exception("unicode value is invalid:" + ch);
				}
				
				// unicode 버퍼에 ch추가
				unicodeStr.append(ch);

				// 상태값을 하나 올림
				// ex) 10:유니코드 시작 -> 11:유니코드 1번째 문자
				status++;
				
				// Unicode escape가 종료(status가 15 이상)되면
				// Unicode를 추가하고, 상태를 일반문자열 상태로 변경함
				if(status >= 15) {
					
					char unicodeCh = (char)Integer.parseInt(unicodeStr.toString(), 16);
					escapedStr.append(unicodeCh);
					
					unicodeStr.delete(0, unicodeStr.length());
					status = 0;
				}
				
			} else {
				throw new Exception("Unexpected status: " + status);
			}
		} // End of for
		
		return escapedStr.toString();
	}
	
	/**
	 * 주어진 문자(ch)가 16진수 값(0-9, A-F, a-f) 인지 확인
	 * 
	 * @param ch 검사할 문자
	 * @return 16진수 값 여부(16진수 값일 경우 true, 아닐 경우 false)
	 */
	private static boolean isHex(char ch) {
		return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F');
	}
	
	/**
	 * 문자열 내에 null(\0)가 포함 여부 반환<br>
	 * 포함되어 있을 경우 true<br>
	 * ex) "test.jsp\0.doc" 일 경우 true
	 * 
	 * @param contents 문자열
	 * @return null(\0) 포함 여부
	 */
	public static boolean hasNull(String contents) throws Exception {
		
		if(contents == null) {
			throw new Exception("contents is null");
		}
		
		for(int index = 0; index < contents.length(); index++) {
			char ch = contents.charAt(index);
			if(ch == '\0') {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 두 문자가 동일한지 비교<br>
	 * 대소문자 구분 여부를 확인하여 두 문자를 비교함
	 * 
	 * @param ch1 비교할 문자 1 
	 * @param ch2 비교할 문자 2
	 * @param ignoreCase 대소문자 구분 여부(true-구분하지 않음, false-구분함)
	 * @return 동일 여부
	 */
	public static boolean isEqualChar(char ch1, char ch2, boolean ignoreCase) {
		
		if(ignoreCase == false) {
			return ch1 == ch2;
		} else {
			return Character.toLowerCase(ch1) == Character.toLowerCase(ch2);
		}
	}
	
	/**
	 * 문자열 내 여러 문자열을 검색하는 메소드<br>
	 * -> 문자열을 한번만 읽어 수행 속도 향상 목적
	 * 
	 * @param contents 문자열
	 * @param ignoreCase 대소문자 구분 여부(true - 구분하지 않음, false - 구분함)  
	 * @param findStrs 검색할 문자열들
	 * @return 최초로 발견된 위치 목록(못찾은 경우 -1)
	 */
	public static int[] find(String contents, boolean ignoreCase, String... findStrs) throws Exception {
		
		// 입력값 검증
		if(contents == null) {
			throw new NullPointerException("contents is null.");
		}
		
		if(findStrs == null) {
			throw new NullPointerException("findStrs is null.");
		}
		
		if(findStrs.length == 0) {
			return new int[]{};
		}
		
		// 검색 문자열들에 대한 정보 객체 변수 선언 및 초기화 수행
		ArrayList<FindStr> findStrObjs = new ArrayList<FindStr>(findStrs.length);
		for(int index = 0; index < findStrs.length; index++) {
			findStrObjs.add(new FindStr(findStrs[index], ignoreCase));
		}
		
		// 대상 문자열을 한문자씩 읽어서 검색 수행
		for(int index = 0; index < contents.length(); index++) {
			
			char ch = contents.charAt(index);
			
			// 검색 문자열 별로 검색
			for(FindStr findStrObj: findStrObjs) {
				findStrObj.process(index, ch);
			}
		}
		
		// 검색 결과를 반환하기 위해 int 배열 형태로 변경
		int[] findLocs = new int[findStrObjs.size()];
		for(int index = 0; index < findLocs.length; index++) {
			findLocs[index] = findStrObjs.get(index).getFindLoc();
		}
		
		// 검색 결과 반환
		return findLocs;
	}
	
	/**
	 * find 메소드에서 사용할 검색 정보 클래스 
	 * 
	 * @author jmsohn
	 */
	@Data
	private static class FindStr {
		
		/** 검색해야할 문자열 */
		private String findStr;
		/** 대소문자 구분 여부(true-구분하지 않음, false-구분함) */
		private boolean ignoreCase;
		
		/** 최초 일치 위치 */
		private int findLoc;
		/**
		 * 검색 중인 문자열 위치 정보
		 * key - 일치 시작 위치, value - 문자열 내에 현재까지 일치하는 위치
		 */
		private Map<Integer, Integer> pins;
		
		/**
		 * 생성자
		 * 
		 * @param findStr 검색해야할 문자열
		 * @param ignoreCase 대소문자 구분 여부(true-구분하지 않음, false-구분함)
		 */
		FindStr(String findStr, boolean ignoreCase) throws Exception {
			
			if(findStr == null) {
				throw new NullPointerException("findStr is null");
			}
			
			this.setFindStr(findStr);
			this.setIgnoreCase(ignoreCase);
			this.setFindLoc(-1);	// 못찾은 경우 -1
			this.setPins(new HashMap<Integer, Integer>());
		}
		
		/**
		 * 입력된 문자에 대해 검색 수행<br>
		 * -> 한문자씩 확인 작업 수행
		 * 
		 * @param index 검색 대상 문자열내에 현재 위치
		 * @param ch 입력된 문자
		 */
		void process(int index, char ch) {
			
			// 이미 찾은 경우 더이상 검색을 수행하지 않음
			if(this.getFindLoc() != -1) {
				return;
			}

			// 문자가 일치하지 않는 경우
			// pin에서 삭제할 대상 목록
			Set<Integer> toRemove = new HashSet<Integer>();
			
			// 각 pin 들에 대해 주어진 문자(ch)와 검색 중인 문자(findCh) 일치 여부를 확인
			for(int startIndex: this.getPins().keySet()) {
				
				int findIndex = this.getPins().get(startIndex);
				char findCh = this.getFindStr().charAt(findIndex);
				
				if(StringUtil.isEqualChar(ch, findCh, this.ignoreCase) == true) {
					
					findIndex++;
					
					// 문자열 일치하는 경우
					// -> findLoc 설정 후 종료
					// 문자열 일치하지 않는 경우
					// -> 하나 증가된 findIndex를 startIndex에 설정
					if(findIndex >= this.getFindStr().length()) {
						this.setFindLoc(startIndex);
						return;
					} else {
						this.getPins().put(startIndex, findIndex);
					}
					
				} else {
					
					// 문자가 일치하지 않을 경우 삭제 대상에 추가
					// 여기에서 삭제하면 for 문이 돌고 있는 중에 대상에 변화가 생겨 오류가 발생
					toRemove.add(startIndex);
				}
			}
			
			// pin 목록에서 pin 삭제
			for(Integer key: toRemove) {
				this.getPins().remove(key);
			}
			
			// 최초 문자와 일치하는 경우 새로운 pin 생성
			if(StringUtil.isEqualChar(ch, this.getFindStr().charAt(0), this.ignoreCase) == true) {
				this.getPins().put(index, 1);
			}
		}
	}
	
	/**
	 * 문자열 내에 검색할 문자열이 하나라도 있는지 확인하는 메소드<br>
	 * ex) contents: "Hello, world", findStrs: {"abc", "world"} 일 경우<br>
	 *     "world"가 포함되어 있으므로 true를 반환
	 * 
	 * @param contents 문자열
	 * @param ignoreCase 대소문자 구분 여부(true - 구분하지 않음, false - 구분함)
	 * @param findStrs 검색할 문자열들
	 * @return 문자열 내에 검색할 문자열이 하나라도 있는지 여부
	 */
	public static boolean containsAny(String contents, boolean ignoreCase, String... findStrs) throws Exception {
		
		int[] indexes = find(contents, ignoreCase, findStrs);
		
		for(int index: indexes) {
			
			// 문자열이 있는 경우 true를 반환
			if(index >= 0) {
				return true;
			}
		}
		
		// 검색된 문자열이 없는 경우 false를 반환
		return false;
	}
	
	/**
	 * 여러 문자열을 구분자(delimiter)를 넣어 이어 붙히는 메소드<br>
	 * ex) delimiter: ",", strs: {"abc", "def"} -> "abc,def"
	 * 
	 * @param delimiter 문자열 목록 사이에 추가할 구분자(null 일경우 "")
	 * @param strs 문자열들
	 * @return 이어 붙힌 문자열
	 */
	public static String join(String delimiter, String... strs) throws Exception {
		
		// 입력값 검증
		if(delimiter == null) {
			delimiter = "";
		}
		
		if(strs == null) {
			return "";
		}
		
		// 문자열을 이어 붙히기 위한 StringBuilder 변수
		StringBuilder joinStr = new StringBuilder("");
		
		// 각 문자열들을 이어 붙힘
		for(int index = 0; index < strs.length; index++) {
			
			if(strs[index] == null) {
				throw new NullPointerException("strs array has null element at " + index);
			}
			
			// 문자열을 이어 붙힘
			// 단 마지막 문자열의 뒤에는 구분자(delimiter)를 붙히지 않음
			joinStr.append(strs[index]);
			if(index + 1 != strs.length) {
				joinStr.append(delimiter);
			}
		}
		
		return joinStr.toString();
	}
	
	/**
	 * 여러 객체의 toString()한 문자열을 구분자(delimiter)를 넣어 이어 붙히는 메소드
	 * 
	 * @param delimiter 구분자
	 * @param iter 여러 객체를 담고 있는 iterator
	 * @return 이어 붙힌 문자열
	 */
	public static String join(String delimiter, Iterable<?> iter) throws Exception {
		
		// iterator에서 문자열 목록을 만듦
		ArrayList<String> strs = new ArrayList<>();
		
		iter.forEach(obj -> {
			strs.add(obj.toString());
		});
		
		return join(delimiter, strs.stream().toArray(String[]::new));
	}
	
	/**
	 * int 배열을 구분자(delimiter)를 넣어 문자열로 만드는 메소드<br>
	 * delimiter: ",", array: {1,2,3} -> "1,2,3"
	 * 
	 * @param delimiter int 목록 사이에 추가할 구분자(null 일경우 "")
	 * @param array 문자열로 변환할 int 배열
	 * @return 변환된 문자열
	 */
	public static String join(String delimiter, int... array) {
		
		if(delimiter == null) {
			delimiter = "";
		}
		
		if(array == null) {
			return "";
		}
		
		StringBuilder builder = new StringBuilder();
		
		for(int index = 0; index < array.length; index++) {
			
			if(index != 0) {
				builder.append(delimiter);
			}
			
			builder.append(array[index]);
		}
		
		return builder.toString();
	}
	
	/**
	 * 문자열의 길이를 반환하는 메소드<br>
	 * -> 주어진 문자열이 null 일 경우 0을 반환함<br>
	 * ex) "abc" 일 경우, 3을 반환<br>
	 *     "" 일 경우, 0을 반환
	 * 
	 * @param str 문자열
	 * @return 문자열의 길이
	 */
	public static int length(String str) {
		
		if(str == null) {
			return 0;
		}
		
		return str.length();
	}

	/**
	 * 문자열이 비어 있는지 반환하는 메소드<br>
	 * -> 주어진 문자열이 null이거나 "" 일 경우 true를 반환함
	 * 
	 * @param str 문자열
	 * @return 문자열이 비어 있는지 여부
	 */
	public static boolean isEmpty(String str) {
		
		if(str == null) {
			return true;
		}
		
		return str.isEmpty();
	}
	
	/**
	 * 문자열이 공란으로 이루어져 있는지 반환하는 메소드<br>
	 * -> 주어진 문자열이 null이거나 "", "   ", "\t  " 등은 true를 반환함
	 * 
	 * @param str 문자열
	 * @return 문자열이 공란으로 이루어져 있는지 여부
	 */
	public static boolean isBlank(String str) {
		
		if(str == null) {
			return true;
		}
		
		return str.matches("\\s*");
	}
	
	/**
	 * 문자열에 공란이 있는지 여부 반환<br>
	 * 빈문자열(empty) 및 문자열이 null 일 경우 true 반환
	 * 
	 * @param str 문자열
	 * @return 공란이 있는지 여부
	 */
	public static boolean hasBlank(String str) {
		
		if(isEmpty(str) == true) {
			return true;
		}
		
		return str.matches(".*\\s.*");
	}

	/**
	 * 문자열을 역전 시켜 반환하는 메소드<br>
	 * ex) abc -> cba 
	 * 
	 * @param str 역전시킬 문자열
	 * @return 역전된 문자열
	 */
	public static String reverse(String str) throws Exception {
		
		if(str == null) {
			throw new NullPointerException("str is null");
		}
		
		return new StringBuilder(str).reverse().toString();
	}
	
	/**
	 * 문자열을 구분자 문자에 의해 나눔, 설정에 따라 나누어진 문자열에 trim, escape 처리함<br>
	 * 단, 문자열에 구분자가 escape 되어 있으면 구분하지 않음<br>
	 * ex) delimiter: ',' 이고<br>
	 *     str: "Test\, 입니다., 두번째 문장" 이면,<br>
	 *     "Test\, 입니다.", " 두번째 문장" 로 분리함
	 * 
	 * @param str 문자열
	 * @param delimiter 구분자
	 * @param isTrim 나누어진 문자열을 trim할 것인지 여부
	 * @param isEscape 나누어진 문자열을 escape할 것인지 여부
	 * @return 나누어진 문자열 목록
	 */
	public static String[] split(String str, char delimiter, boolean isTrim, boolean isEscape) throws Exception {
		
		// 입력값 검증
		if(str == null) {
			throw new NullPointerException("str is null");
		}
		
		// 나누어진 문자열들을 보관하는 변수 
		ArrayList<String> splitedStrs = new ArrayList<String>();
		// 문자열을 나누기 위한 임시 변수
		StringBuilder splitedStrBuffer = new StringBuilder("");
		// 이전 문자가 escape 문자였는지 여부
		boolean isEscapeChar = false;
		
		// 문자열을 끝까지 순회함
		for(int index = 0; index < str.length(); index++) {
			
			char ch = str.charAt(index);
			
			// escape 되지 않은 구분자일 경우, splitedStr을 splitedStrs에 추가함
			// 아닐 경우, splitedStr에 현재 문자를 추가
			if(ch == delimiter && isEscapeChar == false) {
				
				String splitedStr = splitedStrBuffer.toString();
				
				// trim 처리
				if(isTrim == true) {
					splitedStr = splitedStr.trim();
				}
				
				// escape 처리
				if(isEscape == true) {
					splitedStr = escape(splitedStr);
				}
				
				splitedStrs.add(splitedStr);
				splitedStrBuffer.setLength(0); // splitedStr의 내용을 모두 지움
				
			} else {
				splitedStrBuffer.append(ch);
			}
			
			// escape 문자이면 true, 아니면 false
			isEscapeChar = (ch == '\\')?true:false;
		}
		
		// 문자열의 모든 문자에 대해 종료되었을때,
		// splitedStr의 내용을 splitedStrs에 추가함
		String splitedStr = splitedStrBuffer.toString();
		// trim 처리
		if(isTrim == true) {
			splitedStr = splitedStr.trim();
		}
		// escape 처리
		if(isEscape == true) {
			splitedStr = escape(splitedStr);
		}
			
		splitedStrs.add(splitedStr);
		
		return splitedStrs.toArray(new String[splitedStrs.size()]);
	}

	/**
	 * 문자열을 구분자 문자에 의해 나눔, 나누어진 문자열에 trim, escape 처리함<br>
	 * 단, 문자열에 구분자가 escape 되어 있으면 구분하지 않음<br>
	 * ex) delimiter: ',' 이고<br>
	 *     str: "Test\, 입니다., 두번째 문장" 이면,<br>
	 *     "Test\, 입니다.", "두번째 문장" 로 분리함
	 * 
	 * @param str 문자열
	 * @param delimiter 구분자
	 * @return 나누어진 문자열 목록
	 */
	public static String[] split(String str, char delimiter) throws Exception {
		return split(str, delimiter, true, true);
	}

	/**
	 * 주어진 문자열을 delimiter로 나눈 후 특정 위치들의 문자열들을 선택하여 반환<br>
	 * ex) str: test1\ttest2\ttest3, delimiter: \t, locs: {0, 2, 1}<br>
	 *     -> {"test1", "test3", "test2"} 가 반환됨
	 * 
	 * @param str 문자열
	 * @param delimiter 나눌 문자열
	 * @param locs 선택할 위치 목록
	 * @return 선택된 문자열 목록 
	 */
	public static String[] pick(String str, String delimiter, int[] locs) throws Exception {
		
		// 입력값 검증
		if(isEmpty(delimiter) == true) {
			throw new Exception("delimiter is empty");
		}
		
		if(locs == null) {
			throw new NullPointerException("locs is null");
		}
		
		// 선택된 문자열 목록 변수
		String[] picks = new String[locs.length];
		if(isEmpty(str) == true) {
			return picks;
		}
		
		// delimiter로 분해
		String[] splitedStrs = str.split(delimiter);
		
		for(int index = 0; index < locs.length; index++) {
			
			// 현재 loc 값
			int loc = locs[index];
			
			// loc 값이 0 보다 작거나 나누어진 문자열의 수보다 크면
			// 다음 loc 값을 찾음
			if(loc < 0 || loc >= splitedStrs.length) {
				continue;
			}
			
			picks[index] = splitedStrs[loc];
		}
		
		return picks;
	}

	/**
	 * 주어진 문자열을 delimiter로 나눈 후 특정 위치의 문자열을 선택하여 반환
	 * ex) str: test1\ttest2\ttest3, delimiter: \t, loc: 1<br>
	 *     -> "test2" 가 반환됨
	 * 
	 * @param str 문자열
	 * @param delimiter 나눌 문자열
	 * @param loc 선택할 위치
	 * @return 선택된 문자열
	 */
	public static String pick(String str, String delimiter, int loc) throws Exception {
		return pick(str, delimiter, new int[]{loc})[0];
	}
	
	/**
	 * wildcard 패턴 문자열과 일치 여부 반환
	 * 
	 * @param wildcardPattern wildcard 패턴 문자열
	 * @param str 검사할 문자열
	 * @return wildcard 패턴 문자열과 일치 여부
	 */
	public static boolean matchWildcard(String wildcardPattern, String str) throws Exception {
		return WildcardPattern.create(wildcardPattern).match(str).isMatch();
	}
	
	/**
	 * wildcard 패턴 문자열과 일치 여부 반환
	 * 
	 * @param wildcardPattern wildcard 패턴 문자열
	 * @param ignoreCase 대소문자 구별 여부(true-구별하지 않음, false-구별함)
	 * @param str 검사할 문자열
	 * @return wildcard 패턴 문자열과 일치 여부
	 */
	public static boolean matchWildcard(String wildcardPattern, boolean ignoreCase, String str) throws Exception {
		return WildcardPattern.create(wildcardPattern, ignoreCase).match(str).isMatch();
	}
	
	/**
	 * wildcard pattern 클래스<br>
	 * ex) *abc*, ??.txt
	 * 
	 * @author jmsohn
	 */
	public static class WildcardPattern {
		
		/** wildcard pattern 문자열 */
		private String pattern;
		/** 대소문자 구별 여부(true-구별하지 않음, false-구별함) */
		private boolean ignoreCase;
		
		// wildcard match시에만 사용
		/** 매치된 문자열 목록 */
		private ArrayList<String> groups;
		/** 매치된 문자열의 시작 위치 목록 */
		private ArrayList<Integer> starts;
		/** 매치된 문자열의 길이 목록 */
		private ArrayList<Integer> lengths;
		
		/**
		 * 생성자
		 * 
		 * @param pattern wildcard pattern 문자열
		 */
		private WildcardPattern(String pattern, boolean ignoreCase) throws Exception {
			this.pattern = StringUtil.escape(pattern);
			this.ignoreCase = ignoreCase;
		}
		
		/**
		 * 생성 메소드
		 * 
		 * @param pattern wildcard pattern 문자열
		 * @param ignoreCase 대소문자 구별 여부
		 * @return 생성된 wildcard pattern 클래스
		 */
		public static WildcardPattern create(String pattern, boolean ignoreCase) throws Exception {
			return new WildcardPattern(pattern, ignoreCase); 
		}
		
		/**
		 * 생성 메소드
		 * 
		 * @param pattern wildcard pattern 문자열
		 * @return 생성된 wildcard pattern 클래스
		 */
		public static WildcardPattern create(String pattern) throws Exception {
			return new WildcardPattern(pattern, false); 
		}
		
		/**
		 * 문자열을 설정된 wildcard pattern에 매치함
		 * 
		 * @param str 문자열
		 * @return 매치 결과
		 */
		public WildcardMatcher match(String str) {

			// 초기화 실행
			this.groups = new ArrayList<String>();
			this.starts = new ArrayList<Integer>();
			this.lengths = new ArrayList<Integer>();
			
			// 매치 수행
			boolean match = this.match(str, 0, 0);
			
			// WildcardMatcher 생성 후 반환
			return WildcardMatcher.builder()
					.match(match)
					.groups(this.groups)
					.starts(this.starts)
					.lengths(this.lengths)
					.build();
		}
		
		/**
		 * 문자열이 wildcard pattern 매치 수행<br>
		 * 매치된 문자열 목록(groups), 시작 위치 목록(starts), 길이 목록(lengths)를 설정함
		 * 
		 * @param str 문자열
		 * @param wStart wildcard pattern 문자열의 시작 위치
		 * @param strStart 문자열의 읽기 시작 위치
		 * @return 매치 여부
		 */
		private boolean match(String str, int wStart, int strStart) {
			
			// 입력값 검증
			if(str == null) {
				return false;
			}
			
			if(this.pattern == null) {
				return false;
			}
			
			// 패턴을 분리
			// ex) wildcardPattern="abc*xyz?def", wStart=3 이면
			//     "*" 수량자(lowerSize, upperSize)와 "xyz"(패턴문자열)를 분리해냄
			
			// 고정 문자열 패턴(상기 예에서 "abc")을 저장하기 위한 변수
			StringBuilder patternBuilder = new StringBuilder();
			// wildcard 패턴 문자열 내에서의 위치 변수
			int wPos = wStart;
			// 상태 변수 - 0: 수량자 상태, 1: 패턴 문자열
			int status = 0;
			// 수량자의 최소 크기 변수
			int lowerSize = 0;
			// 수량자의 최대 크기 변수
			int upperSize = 0;
			// 마지막 패턴 여부 변수
			boolean isLastPattern = false;
			
			while(wPos < this.pattern.length()) {
				
				char ch = this.pattern.charAt(wPos);
				
				if(status == 0) {
					
					if(ch == '*') {
						
						upperSize = Integer.MAX_VALUE;
						
					} else if(ch == '?') {
						
						lowerSize++;
						
					} else {
						
						patternBuilder.append(ch);
						status = 1;
					}
					
				} else {
					
					if(ch == '*' || ch == '?') { // 새로운 패턴이 뒤에 있으면 중단
						break;
					} else {
						patternBuilder.append(ch);
					}
				}
				
				wPos++;
			}
			
			// 마지막 패턴 여부 설정
			if(wPos >= this.pattern.length()) {
				isLastPattern = true;
			}
			
			// 최소 크기(lowerSize)가 최대 크기(upperSize) 보다 크면
			// 최대 크기를 최소 크기로 설정함
			if(lowerSize > upperSize) {
				upperSize = lowerSize;
			}
			
			String pattern = patternBuilder.toString();
			
			// 패턴 문자열 매칭 수행
			// ex) 상기 과정에서 분리해낸 "xyz"가 있는지 찾음
			//     즉, wildcardPattern="abc*xyz?def", str="abcdefxyz1def", strStart=3 이면
			//     "defxyz"까지 매칭함
			
			// 매칭이 시작된 지점 위치 변수
			int mark = 0;
			// 패턴 문자열 내에서 매칭 중인 위치 변수
			int pos = 0;
			// 매치 여부 변수
			boolean isMatch = false;
			// 패턴 문자열에 매치되기 이전의 문자의 개수
			// ex) 상기 예의 "defxyz"에서 "def"의 개수 즉 3을 저장
			int count = 0;
			
			if(pattern.length() != 0 && isLastPattern == false) {
				
				// 수량자가 있고 뒤에 패턴 문자열이 있고
				// 마지막 패턴이 아닌 경우
				// -> "*abc?def"에서 "*abc" 인 경우임
				//    "abc" 부분이 매칭되는 경우를 먼저 찾아
				//    검색 시작 지점에서 "abc" 매칭 위치를 뺀 결과가
				//    수량자 설정에 맞는 지 확인 -> 안맞으면 false로 반환
				
				// 남은 대상 문자열에서 검색 위치 변수
				int index = strStart + lowerSize;
				
				for(;index < str.length();index++) {
					
					char ch = str.charAt(index);
					
					// 읽은 문자와 패턴 문자열의 문자와 동일한 지 확인
					if(StringUtil.isEqualChar(ch, pattern.charAt(pos), this.ignoreCase) == true) {
						// 패턴 문자열의 문자와 일치하는 경우
						
						// 패턴의 첫번재 문자가 일치하는 경우
						// 현재 위치를 mark에 저장
						if(pos == 0) {
							mark = index;
						}
						
						// 패턴 문자열의 위치를 다음(오른쪽) 위치로 하나 이동
						pos++;
						
						// 만일, 모든 패턴 문자열이 일치하면 중지함
						if(pos == pattern.length()) {
							isMatch = true;
							break;
						}
						
					} else {
						// 패턴 문자열의 문자와 일치하지 않는 경우
						
						// 패턴 문자열과 일부 일치하지 않은 경우
						// mark 위치로 돌린 다음 다시 검사하도록 함
						if(pos != 0) {
							index = mark;
						}
						
						pos = 0;
					}
				}
				
				// 패턴 문자열 이전의 문자의 수 계산
				count = mark - strStart;
				
				// 문자열이 매치되지 않으면 false 반환
				if(isMatch == false) {
					return false;
				}
				
				// 수량자와 패턴이외에 매칭된 문자 개수와 비교하여 적합하지 않으면 false 반환
				if(count < lowerSize || count > upperSize) {
					return false;
				}
				
				// 수량자가 없는 경우를 제외하고, 매치된 문자열에 관한 정보를 추가함
				// upperSize == 0일 경우 수량자가 없는 경우임
				if(upperSize != 0) {
					this.groups.add(str.substring(strStart, mark));
					this.starts.add(strStart);
					this.lengths.add(count);
				}
				
				// 다음 문자열 매치 검사를 위한 재귀 호출
				return match(str, wPos, index+1);
				
			} else if(pattern.length() != 0 && isLastPattern == true) {
				
				// 수량자가 있고 뒤에 패턴 문자열이 있고
				// 마지막 패턴인 경우
				// -> "*abc?def"에서 "?def" 인 경우임
				//    뒤에서 부터 일치하는지 확인
				//    만일 일치하지 않으면 false를 반환
				//    일치하는 경우, 수량자 확인하여 반환
				
				// 패턴의 뒤쪽 부터 매칭 수행
				int index = 0;
				for(;index < pattern.length(); index++) {

					// 만일, 대상 문자열 보다 패턴 문자열이 크면 false를 반환
					if(str.length() - 1 - index == -1) {
						return false;
					}
					
					// 현재 위치의 패턴 문자 
					char patternCh = pattern.charAt(pattern.length() - 1 - index);
					// 현재 위치의 대상 문자열 문자
					char strCh = str.charAt(str.length() - 1 - index);
					
					// 패턴 문자와 대상 문자열 문자가 일치하지 않으면 false를 반환
					if(StringUtil.isEqualChar(patternCh, strCh, this.ignoreCase) == false) {
						return false;
					}
				}
				
				// 문자 개수 계산
				count = str.length() - index - strStart;
				
				// 수량자가 적합한지 확인하여 반환
				return count >= lowerSize && count <= upperSize;
				
			} else {
				
				// 수량자만 있는 경우
				// 이 경우는 패턴의 마지막에서만 발생함
				// -> "abc*" 일 경우 "*"
				//    뒤에 수량자가 맞는지만 확인하여 반환
				
				// 문자의 수 계산
				count = str.length() - strStart;
				
				// 수량자와 패턴이외에 매칭된 문자 개수와 비교하여 적합하지 않으면 false 반환
				if(count < lowerSize || count > upperSize) {
					return false;
				} else {
					
					// 수량자가 없는 경우를 제외하고, 매치된 문자열에 관한 정보를 추가함
					// upperSize == 0일 경우 수량자가 없는 경우임
					if(upperSize != 0) {
						this.groups.add(str.substring(strStart, str.length()));
						this.starts.add(strStart);
						this.lengths.add(count);
					}
					
					return true;
				}
			}
		} // End of match method
	}
	
	/**
	 * wildcard pattern 매칭 결과 클래스
	 * 
	 * @author jmsohn
	 */
	@Getter
	public static class WildcardMatcher {
		
		/** 매치 여부 */
		private boolean match;
		/** 매치된 문자열 목록 */
		private ArrayList<String> groups;
		/** 매치된 문자열의 시작 위치 목록 */
		private ArrayList<Integer> starts;
		/** 매치된 문자열의 길이 목록 */
		private ArrayList<Integer> lengths;
		
		/**
		 * 생성자
		 * 
		 * @param match 매치 여부
		 * @param groups 매치된 문자열
		 * @param starts 매치된 문자열의 시작 위치 목록
		 * @param lengths 매치된 문자열의 길이 목록
		 */
		@Builder
		public WildcardMatcher(boolean match, ArrayList<String> groups, ArrayList<Integer> starts, ArrayList<Integer> lengths) {
			this.match = match;
			this.groups = groups;
			this.starts = starts;
			this.lengths = lengths;
		}
	}
	
	/**
	 * 문자열 객체의 내용을 수정하는 메소드<br>
	 * - 문자열은 불변(immutable)으로 내용을 수정할 수 없음<br>
	 *   보안상 문제가 되는 문자열도 메모리상에 남아 있음<br>
	 *   이에, 문자열의 사용 이후 보안 문자를 메모리상에서 완전히 삭제 또는 마스킹하는 방법을 제공함<br>
	 * - 이 메소드를 사용할 경우 illegal access warning이 발생함
	 *   이는 java 실행시 "--illegal-access=warn" 옵션을 추가하여 방지할 수 있음
	 * 
	 * @param str 수정할 문자열
	 * @param toStr 변경할 문자열
	 */
	public static void changeStr(String str, String toStr) throws Exception {
		
		// 입력값 검증
		if(str == null) {
			throw new NullPointerException("str is null");
		}
		
		if(toStr == null) {
			throw new NullPointerException("toStr is null");
		}
		
		// 문자열 필드에 변경할 문자열을 강제 설정
		char[] buffer = new char[toStr.length()];
		toStr.getChars(0, toStr.length(), buffer, 0);
		
		TypeUtil.setField(str, "value", buffer);
	}
	
	/**
	 * 문자열 내에 ${변수}을 변수 목록(vars)에서 찾아 대체하는 메소드<br>
	 * str: "${count}회 발생하였습니다." vars: "count": "2" -> "2회 발생하였습니다."
	 *
	 * @param str 변수가 포함된 문자열
	 * @return 변수 목록
	 */
	public static String replaceVars(String str, Map<String, String> values) {

		// 재구성 문자열 변수
		StringBuilder replacedStr = new StringBuilder();
		// ${변수}의 변수 명을 담을 임시 변수
		StringBuilder var = new StringBuilder();

		// 상태 변수, false: 일반 문자열 상태, true: 변수명 상태
		boolean isVar = false;
		for (int index = 0; index < str.length(); index++) {  // 한 문자씩

			char ch = str.charAt(index);

			if (isVar == true) {
				// 변수 상태

				if (ch == '}') {
					// 변수 종료
                    
					// 변수에 설정된 값으로 추가
					replacedStr.append(values.get(var.toString()));
                        
					// 변수명 초기화
					var.setLength(0);
                    
					// 일반 문자열 상태로 상태 변경 
					isVar = false;

				} else if(
						(ch >= 'a' && ch <= 'z') ||
						(ch >= 'A' && ch <= 'Z') ||
						(ch >= '0' && ch <= '9') ||
						ch == '_'
					) {
                	
					// 변수명 내
					// 변수명에 문자 추가
					var.append(ch);

				} else {
                	
					// 변수명이 아닌 문자가 들어올 경우
                	
					// 현재까지 임시저장한 문자열은 일반 문자열이므로
					// 재구성 문자열에 추가함
					replacedStr.append("${").append(var);
                	
					// 한칸 뒤로 이동하여 다시 파싱(pushback)
					index--;

					// 변수명 초기화
					var.setLength(0);
                	
					// 일반 문자열 상태로 변환
					isVar = false;
				}
                
			} else {
            	
				// 일반 문자열 상태
				if (ch == '$' && str.charAt(index+1) == '{' && index+1 < str.length()) {
                	
					// "${" 가 들어온 경우 변수명 상태로 변환
					isVar = true;
					index++;
					
				} else {
					replacedStr.append(ch);
				}
			}
		}
		
		// 파싱 중인 변수명이 있으면 추가함
		if(var.length() != 0) {
			replacedStr.append("${").append(var);
		}
        
		return replacedStr.toString();
	}
	
	/**
	 * 문자열 내에 ${변수} 목록을 반환하는 메소드<br>
	 * str: "${count}회 발생하였습니다." -> {"count"} 반환
	 *
	 * @param str 변수가 포함된 문자열
	 * @return 변수 목록
	 */
	public static ArrayList<String> findAllVars(String str) {

		// 변수명을 순서대로 담을 목록
		ArrayList<String> vars = new ArrayList<>();
		// ${변수}의 변수 명을 담을 임시 변수
		StringBuilder var = new StringBuilder();

		// 상태 변수, false: 일반 문자열 상태, true: 변수명 상태
		boolean isVar = false;
		for (int index = 0; index < str.length(); index++) {  // 한 문자씩

			char ch = str.charAt(index);
            
			if (isVar == true) {
				// 변수 상태

				if (ch == '}') {
					// 변수 종료

					// 변수 목록에 추가
					vars.add(var.toString());
                        
					// 변수명 초기화
					var.setLength(0);

					// 일반 문자열 상태로 상태 변경 
					isVar = false;
                    
				} else if(
						(ch >= 'a' && ch <= 'z') ||
						(ch >= 'A' && ch <= 'Z') ||
						(ch >= '0' && ch <= '9') ||
						ch == '_'
					) {
					// 변수명 내
					// 변수명에 문자 추가
					var.append(ch);

				} else {
                	
					// 변수명이 아닌 문자가 들어올 경우
					// 변수명 초기화
					var.setLength(0);
					isVar = false;
				}

			} else {
            	
				// 일반 문자열 상태
				if (ch == '$' && str.charAt(index+1) == '{' && index+1 < str.length()) {

					// "${" 가 들어온 경우 변수명 상태로 변환
					isVar = true;
					index++;
				}
			}
		}
        
		return vars;
    }
	
	// -------------
	
	/**
	 * target 문자열 내에 패턴(pattern)이 처음 나타난 곳까지의 문자열과<br>
	 * 패턴이 나타난 이후의 문자열을 분리하여 반환<br>
	 * 만일, target 문자열 내에 패턴이 나타나지 않으면 target 문자열만 반환함<br>
	 * <pre>
	 * ex) target: "test1 > test2> test3",
	 *     pattern: "\\s>\\s" 이면
	 *     결과: {"test1", "test2> test3"}
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
	public static String[] splitFirst(String target, String pattern) throws Exception {
		return splitFirstN(target, pattern, 1);
	} // End of splitFirst
	
	
	/**
	 * target 문자열 내에 처음부터 n회 패턴(pattern)으로 나누어 반환<br>
	 * 만일, target 문자열 내에 패턴이 나타나지 않으면 target 문자열만 반환함<br>
	 * <pre>
	 * ex) target: "test1 > test2> test3",
	 *     pattern: "\\s>\\s",
	 *     n: 1이면
	 *     결과: {"test1", "test2> test3"}
	 *     
	 *     target: "test1 > test2> test3 > test4",
	 *     pattern: "\\s>\\s",
	 *     n: 2이면
	 *     결과: {"test1", "test2", "test3 > test4"}
	 *     
	 *     taget: "test1 > test2"
	 *     pattern: "\\sA\\s" 이면
	 *     결과: {"test1 > test2"}
	 * </pre>
	 * 
	 * @param target 대상 문자열
	 * @param pattern 나눌 패턴
	 * @param n 나눌 횟수
	 * @return 분리된 문자열 배열
	 */
	public static String[] splitFirstN(String target, String pattern, int n) throws Exception {
		
		// 입력값 검증
		if(target == null) {
			throw new NullPointerException("target is null.");
		}
		
		if(pattern == null) {
			throw new NullPointerException("pattern is null");
		}
		
		if(n < 1) {
			throw new IllegalArgumentException("n must be greater than 0:" + n);
		}
		
		// 분리된 문자열을 저장할 변수
		ArrayList<String> splited = new ArrayList<String>();
		
		// target 문자열에 패턴 적용
		Pattern patternP = Pattern.compile(pattern);
		Matcher patternM = patternP.matcher(target);

		// 패턴 검사 시작 위치 변수
		int start = 0;
		
		// 패턴이 존재하는지 검사
		while(patternM.find(start) == true && n > 0) {
			
			// 패턴 검사 시작 위치(start)에서 패턴이 발견된 위치(pStart)
			// 문자열을 잘라서 splited 배열에 추가
			int pStart = patternM.start();
			splited.add(target.substring(start, pStart));
			
			// 매치된 마지막 위치에서 다음 패턴 검사를 수행할 수 있도록
			// 패턴 검사 시작 위치(start)를 설정
			start = patternM.end();
			
			// 나누는 횟수를 하나 줄임
			n--;
		}
		
		// start가 0이 아닌 경우는 마지막 남은 문자열을 추가함
		// start가 0인 경우는 매치되는 패턴이 없는 경우임
		splited.add(target.substring(start, target.length()));
		
		// 분리된 문자열 목록을 반환
		return TypeUtil.toArray(splited, String.class);
		
	} // End of splitFirstN
	
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
	
	/**
	 * 문자열에 대한 Reader 객체를 생성
	 * 
	 * @param str 대상 문자열
	 * @param charset 문자열의 character set
	 * @return 생성된 Reader 객체
	 */
	public static Reader newReader(String str, Charset charset) throws Exception {
		
		if(str == null) {
			throw new IllegalArgumentException("str is null.");
		}
		
		if(charset == null) {
			throw new IllegalArgumentException("charset is null.");
		}
		
		return new InputStreamReader(new ByteArrayInputStream(str.getBytes(charset)));
	}
	
	/**
	 * 문자열에 대한 Reader 객체를 생성
	 * 
	 * @param str 대상 문자열
	 * @return 생성된 Reader 객체
	 */
	public static Reader newReader(String str) throws Exception {
		
		if(str == null) {
			throw new IllegalArgumentException("str is null.");
		}
		
		return new InputStreamReader(new ByteArrayInputStream(str.getBytes()));
	}
	
	// -------------
	
	/**
	 * 문자열이 여러 라인일 경우 각 라인에 대해 trim을 수행하는 메소드<br>
	 * ex) " hello \r\n  world! " 일 경우, "hello\r\nworld!" 반환
	 * 
	 * @param str trim 대상 문자열
	 * @return 라인별 trim 수행한 결과
	 */
	public static String trimMultiLine(String str) throws Exception {
		return trimMultiLine(str, TrimType.TRIM, false);
	}

	/**
	 * 문자열이 여러 라인일 경우 각 라인에 대해 trim을 수행하는 메소드<br>
	 * ex) " hello \r\n  world! " 일 경우, "hello\r\nworld!" 반환
	 * 
	 * @param str trim 대상 문자열
	 * @param removeBlankLine 만일 빈 문자열일 경우 삭제할 것인지 여부
	 * @return 라인별 trim 수행한 결과
	 */
	public static String trimMultiLine(String str, boolean removeBlankLine) throws Exception {
		return trimMultiLine(str, TrimType.TRIM, removeBlankLine);
	}
	
	/**
	 * 문자열이 여러 라인일 경우 각 라인에 대해 left trim을 수행하는 메소드<br>
	 * ex) " hello \r\n  world! " 일 경우, "hello \r\nworld! " 반환
	 * 
	 * @param str left trim 대상 문자열
	 * @return 라인별 left trim 수행한 결과
	 */
	public static String ltrimMultiLine(String str) throws Exception {
		return trimMultiLine(str, TrimType.L_TRIM, false);
	}
	
	/**
	 * 문자열이 여러 라인일 경우 각 라인에 대해 left trim을 수행하는 메소드<br>
	 * ex) " hello \r\n  world! " 일 경우, "hello \r\nworld! " 반환
	 * 
	 * @param str left trim 대상 문자열
	 * @param removeBlankLine 만일 빈 문자열일 경우 삭제할 것인지 여부
	 * @return 라인별 left trim 수행한 결과
	 */
	public static String ltrimMultiLine(String str, boolean removeBlankLine) throws Exception {
		return trimMultiLine(str, TrimType.L_TRIM, removeBlankLine);
	}
	
	/**
	 * 문자열이 여러 라인일 경우 각 라인에 대해 left trim을 수행하는 메소드<br>
	 * ex) " hello \r\n  world! " 일 경우, " hello\r\n  world!" 반환
	 * 
	 * @param str right trim 대상 문자열
	 * @return 라인별 right trim 수행한 결과
	 */
	public static String rtrimMultiLine(String str) throws Exception {
		return trimMultiLine(str, TrimType.R_TRIM, false);
	}
	
	/**
	 * 문자열이 여러 라인일 경우 각 라인에 대해 left trim을 수행하는 메소드<br>
	 * ex) " hello \r\n  world! " 일 경우, " hello\r\n  world!" 반환
	 * 
	 * @param str right trim 대상 문자열
	 * @param removeBlankLine 만일 빈 문자열일 경우 삭제할 것인지 여부
	 * @return 라인별 right trim 수행한 결과
	 */
	public static String rtrimMultiLine(String str, boolean removeBlankLine) throws Exception {
		return trimMultiLine(str, TrimType.R_TRIM, removeBlankLine);
	}

	/**
	 * trim 종류
	 */
	private enum TrimType {
		/** 양방향 trim */
		TRIM,
		/** 왼쪽 trim */
		L_TRIM,
		/** 오른쪽 trim */
		R_TRIM
	}
	
	/**
	 * 문자열이 여러 라인일 경우 각 라인에 대해 trim을 수행하는 메소드<br>
	 * ex) " hello \r\n  world! " 일 경우, "hello\r\nworld!" 반환
	 * 
	 * @param str trim 대상 문자열
	 * @param type trim 종류
	 * @param removeBlankLine 만일 빈 문자열일 경우 삭제할 것인지 여부
	 * @return 라인별 trim 수행한 결과
	 */
	private static String trimMultiLine(String str, TrimType type, boolean removeBlankLine) throws Exception {
		
		if(type == null) {
			throw new Exception("trim type is null.");
		}
		
		if(str == null) {
			return null;
		}
		
		StringBuilder trimedStr = new StringBuilder("");
		
		int index = 0;
		while(index < str.length()) {
			index = trimOneLine(str, type, index, trimedStr, removeBlankLine);
		}
		
		return trimedStr.toString();
	}
	
	/**
	 * 한 라인의 문자열에 대해 trim 수행<br>
	 * [\r]\n 까지 읽어서 trim 작업을 수행하고, [\r]\n 을 마지막에 추가함 
	 * 
	 * @param str trim 대상 문자열
	 * @param type trim 종류
	 * @param index 읽기 시작해야할 위치
	 * @param trimedStr trim 된 문자열을 담을 버퍼 객체
	 * @param removeBlankLine 만일 빈 문자열일 경우 삭제할 것인지 여부
	 * @return 다음에 읽을 문자열 위치
	 */
	private static int trimOneLine(String str, TrimType type, int index, StringBuilder trimedStr, boolean removeBlankLine) throws Exception {
		
		// 공란을 담아 두는 임시 버퍼
		StringBuilder buffer = new StringBuilder("");

		// 읽은 문자
		char ch = '\0';
		
		// carriage return(\r) 이 나타났는지 여부 변수
		// "\r\n" 을 처리하기 위함
		boolean isCRPresent = false;
		
		// ---- 문자열 왼쪽 부분에 대한 처리 
		for(; index < str.length(); index++) {
			
			ch = str.charAt(index);
			
			// 개행 문자(\n)가 들어온 경우,
			// 이 행은 빈문자열(blank)이므로 개행 문자를 추가하여 반환
			if(ch == '\n') {
				
				// removeBlankLine이 설정되어 있으면 현재 라인을 추가하지 않음
				if(removeBlankLine == false) {
					
					// carriage return 이 이전에 있었을 경우,
					// '\r' 을 추가함
					if(isCRPresent == true) {
						trimedStr.append('\r');
					}
					
					trimedStr.append(ch);
				}
				
				index++;	// 현재 문자를 추가했기 때문에, 다음 문자를 보기 위해 index를 추가함
				return index;
			}
			
			// 공백이 아닌 문자열이 들어온 경우 for문 종료
			if(isBlankChar(ch) == false) {
				buffer.append(ch);
				index++;	// 현재 문자를 추가했기 때문에, 다음 문자를 보기 위해 index를 추가함
				break;
			}

			// 오른쪽 trim(R_TRIM)일 경우, 공백 문자를 버퍼에 추가함
			// 이외의 경우에는 공백 문자를 추가하지 않음
			if(type == TrimType.R_TRIM) {
				buffer.append(ch);
			}
			
			// carriage return 이 나타날 경우 
			if(ch == '\r') {
				isCRPresent = true;
			} else {
				isCRPresent = false;
			}
		}
		
		// 문자열 오른쪽 버퍼의 내용을 추가함
		trimedStr.append(buffer);
		
		// 버퍼 클리어
		buffer.setLength(0);
		
		// ---- 문자열 가운데 및 왼쪽 부분에 대한 처리
		for(; index < str.length(); index++) {
			
			ch = str.charAt(index);
			
			// 개행 문자일 경우, for 문 종료
			if(ch == '\n') {
				index++;	// 현재 문자를 추가했기 때문에, 다음 문자를 보기 위해 index를 추가함
				break;
			}
			
			// 공백이 아닌 경우, 버퍼 내용과 현재 문자 추가
			// 공백인 경우 버퍼에 문자 추가
			if(isBlankChar(ch) == false) {
				
				// 버퍼의 내용이 있으면 추가 후 버퍼 클리어
				if(buffer.length() != 0) {
					trimedStr.append(buffer);
					buffer.setLength(0);
				}
				
				// 현재 문자 추가
				trimedStr.append(ch);
				
			} else {
				
				// 공백을 버퍼에 추가
				buffer.append(ch);
			}
			
			// carriage return 이 나타날 경우 
			if(ch == '\r') {
				isCRPresent = true;
			} else {
				isCRPresent = false;
			}
		}
		
		// 만일 왼쪽 trim(L_TRIM)이면, 버퍼의 내용을 뒤(오른쪽)에 붙힘
		if(type == TrimType.L_TRIM) {
			trimedStr.append(buffer);
		}
		
		// 만일 개행문자로 종료되었을 경우, 개행문자를 추가함
		if(ch == '\n') {
			
			if(isCRPresent == true) {
				trimedStr.append('\r');
			}
			
			trimedStr.append(ch);
		}
		
		return index;
	}
	
	/**
	 * 공백 문자 여부 반환(trimOneLine에서만 사용)<br>
	 * 공백 문자일 경우 true
	 * 
	 * @param ch 검사 대상 문자
	 * @return 공백 문자 여부
	 */
	private static boolean isBlankChar(char ch) {
		return ch == ' ' || ch == '\t' || ch == '\0' || ch == '\r';
	}
}
