package com.redeye.appagent.script.parserfw;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;

import lombok.Getter;

/**
 * 전이 함수 클래스
 * 
 * @author jmsohn
 */
public class Transfer {
	
	/** 문자 패턴 변수 */
	private CharPattern pattern;
	
	/** 전이할 다음 상태 변수 */
	@Getter
	private String nextStatus;
	
	/** 상태 전이시 pushback 수행 여부 */
	@Getter
	private int pushback;
	
	/**
	 * 생성자
	 * 
	 * @param patternStr 다음 전이 상태로 이동시킬 문자 패턴
	 * @param nextStatus 다음 전이 상태
	 * @param pushback
	 */
	public Transfer(String patternStr, String nextStatus, int pushback) throws Exception {
		this.pattern = CharPatternParser.parse(patternStr);
		this.nextStatus = nextStatus;
		this.pushback = pushback;
	}
	
	/**
	 * 주어진 문자가 문자 패턴에 일치하는지 검사하여 반환
	 * 
	 * @param ch 문자
	 * @return 일치 여부
	 */
	public boolean isValid(char ch) {
		return this.pattern.isValid(ch);
	}

	/**
	 * 문자 패턴 클래스
	 * 
	 * @author jmsohn
	 */
	private static class CharPattern {
		
		/** not 조건 변수 */
		private boolean not = false;
		/** 문자 패턴 목록 변수 */
		private ArrayList<PatternChecker> checkers;
		
		/**
		 * 생성자
		 * 
		 * @param checkers 문자 패턴 목록
		 * @param not not 조건
		 */
		private CharPattern(ArrayList<PatternChecker> checkers, boolean not) {
			this.checkers = checkers;
			this.not = not;
		}
		
		/**
		 * 문자가 문자 패턴에 일치하는지 여부 검사
		 * 
		 * @param ch 검사할 문자
		 * @return 일치 여부
		 */
		private boolean isValid(char ch) {
			
			if(this.checkers.size() == 0) {
				return true;
			}
			
			//
			boolean result = false;
			
			for(PatternChecker checker: this.checkers){
				if(checker.isValid(ch) == true) {
					result = true;
					break;
				}
			}
			
			if(this.not == false) {
				return result;
			} else {
				return !result;
			}
		}
		
		/**
		 * CharPattern의 객체의 정보를 문자열로 반환
		 * 
		 * @return 객체 정보 문자열
		 */
		public String toString() {
			
			StringBuffer buffer = new StringBuffer("");
			
			buffer.append("not=").append(this.not).append("\n");
			
			for(PatternChecker checker: this.checkers) {
				buffer.append(checker.toString()).append("\n");
			}
			
			return buffer.toString();
		}
	}
	
	/**
	 * 문자 패턴 검사 클래스
	 * 
	 * @author jmsohn
	 */
	private static interface PatternChecker {
		public boolean isValid(char ch);
	}
	
	/**
	 * 한문자와 일치하는지 검사하는 클래스
	 * 
	 * @author jmsohn
	 */
	private static class CharPatternChecker implements PatternChecker {
		private char ch;
		
		CharPatternChecker(char ch) {
			this.ch = ch;
		}
		
		public boolean isValid(char ch) {
			return this.ch == ch;
		}
		
		public String toString() {
			return "char: '" + this.ch + "'";
		}
	}
	
	/**
	 * 모든 문자 일치(.) 클래스
	 * 
	 * @author jmsohn
	 */
	private static class DotPatternChecker implements PatternChecker {
		
		public boolean isValid(char ch) {
			return true;
		}
		
		public String toString() {
			return "dot";
		}
	}
	
	/**
	 * 문자가 범위내에 있는지 검사하는 클래스
	 * 
	 * @author jmsohn
	 */
	private static class RangePatternChecker implements PatternChecker{
		
		/** 범위 중 아래측 변수 */
		private char lowerCh;
		/** 범위 중 위측 변수 */
		private char upperCh;
		
		/**
		 * 
		 * @param lowerCh 범위 중 아래측 값
		 * @param upperCh 범위 중 위측 값
		 */
		RangePatternChecker(char lowerCh, char upperCh) {
			this.lowerCh = lowerCh;
			this.upperCh = upperCh;
		}

		/**
		 * 
		 * @param ch
		 * @return
		 */
		public boolean isValid(char ch) {
			return this.lowerCh <= ch && ch <= this.upperCh;
		}
		
		public String toString() {
			return "range '" + this.lowerCh + "' - '" + this.upperCh + "'";
		}
	}
	
	/**
	 * 
	 * 
	 * @author jmsohn
	 */
	private static class CharPatternParser {
		
		/**
		 * 
		 * @author jmsohn
		 */
		enum ParsingStatus {
			START,
			CHAR,
			ESCAPE,
			LOWER_CHAR,	// START OF RANGE
			LOWER_ESCAPE,
			RANGE_MARK,
			UPPER_CHAR,
			UPPER_ESCAPE
		}
		
		/**
		 * 
		 * @param patternStr
		 * @return
		 */
		static CharPattern parse(String patternStr) throws Exception {
			
			ArrayList<PatternChecker> checkers = new ArrayList<PatternChecker>();
			
			PushbackReader in = new PushbackReader(new StringReader(patternStr), 16);
			int read = in.read();
			
			// not 연산 여부
			boolean not = false;
			
			//
			boolean isEscaped = false;
			int preChar = -1;
			
			//
			ParsingStatus status = ParsingStatus.START;
			
			while(read != -1) {
				
				char ch = (char)read;
				
				switch(status) {
				case START:
					
					if(ch == '^') {
						not = true;
					} else if (ch == '-') {
						throw new Exception("Unexpected char: -");
					} else {
						in.unread(read);
					}
					
					status = ParsingStatus.CHAR;
					
					break;
					
				case CHAR:
					
					if(ch == '\\') {
						
						status = ParsingStatus.ESCAPE;
						
					} else if (ch == '-') {
						
						in.unread(read);
						in.unread(preChar);
						
						if(isEscaped == true) {
							in.unread((int)'\\');
						}
						
						status = ParsingStatus.LOWER_CHAR;
						
					} else if(ch == '.') {
						
						checkers.add(new Transfer.DotPatternChecker());
						
						isEscaped = false;
						preChar = read;
						
					} else {
						
						checkers.add(new Transfer.CharPatternChecker(ch));
						
						isEscaped = false;
						preChar = read;

					}
					
					break;
					
				case ESCAPE:
					
					checkers.add(new Transfer.CharPatternChecker(ch));
					
					isEscaped = true;
					preChar = read;
					status = ParsingStatus.CHAR;
					
					break;
					
				case LOWER_CHAR:
					
					if(ch == '\\') {
						status = ParsingStatus.LOWER_ESCAPE;
					} else if(ch == '.' || ch == '-') {
						throw new Exception("Unexpected Char: " + ch);
					} else {
						preChar = read;
						status = ParsingStatus.RANGE_MARK;
					}
					
					break;
				
				case LOWER_ESCAPE:
					
					preChar = read;
					status = ParsingStatus.RANGE_MARK;
					
					break;
					
				case RANGE_MARK:
					
					if(ch != '-') {
						throw new Exception("Unexpected Char: " + ch);
					}
					
					status = ParsingStatus.UPPER_CHAR;
					
					break;
					
				case UPPER_CHAR:
					
					if(ch == '\\') {
						status = ParsingStatus.UPPER_ESCAPE;
					} else {
						checkers.add(new Transfer.RangePatternChecker((char)preChar, ch));
						status = ParsingStatus.CHAR;
					}
					
					break;
					
				case UPPER_ESCAPE:
					checkers.add(new Transfer.RangePatternChecker((char)preChar, ch));
					status = ParsingStatus.CHAR;
					break;
					
				}
				
				read = in.read();
			}
			
			if(status != ParsingStatus.CHAR && status != ParsingStatus.START) {
				throw new Exception("invalid end status: " + status);
			}
			
			return new CharPattern(checkers, not);
		}
		
	}

}