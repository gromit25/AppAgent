package com.redeye.appagent.script;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.redeye.appagent.script.instructions.AbstractExp;
import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.olexp.parser.BooleanParser;
import com.redeye.appagent.script.parserfw.AbstractParser;

/**
 * 표현식을 포함한 텍스트 형식 문자열과 변수 목록으로 텍스트를 생성하는 클래스<br>
 * ex) 텍스트 형식: "severity: ${if(severity == 'fatal', 'red', 'yellow')} ", 변수 목록: {"severity":"fatal"}<br>
 *     -> "severity: red "
 * 
 * @author jmsohn
 */
public class TextGen {
	
	/**
	 * 텍스트 요소 인터페이스<br>
	 * 텍스트와 표현식 모두 이 인터페이스를 구현함
	 * 
	 * @author jmsohn
	 */
	private static interface TextElement {
		
		/**
		 * 변수 목록을 이용하여 텍스트를 생성하여 반환
		 * 
		 * @param values 변수 목록
		 * @return 생성된 텍스트
		 */
		public String getText(Map<String, ?> values) throws Exception;
	}
	
	/**
	 * 텍스트 내의 표현식 처리를 위한 텍스트 요소 클래스<br>
	 * ex) "severity: ${if(severity == 'fatal', 'red', 'yellow')}" 에서 ${if(severity == 'fatal', 'red', 'yellow')}에 대한 처리
	 * 
	 * @author jmsohn
	 */
	private static class ExpElement extends AbstractExp implements TextElement{
		
		/**
		 * 생성자
		 */
		private ExpElement(String exp) throws Exception {
			super(exp);
		}

		@Override
		protected AbstractParser<Instruction> getRootParser() throws Exception {
			return new BooleanParser();
		}
		
		/**
		 * 생성 메소드
		 * 
		 * @param exp 명령어 문자열
		 * @return 생성된 명령어 처리 클래스
		 */
		public static ExpElement compile(String exp) throws Exception {
			return new ExpElement(exp);
		}
		
		@Override
		public String getText(Map<String, ?> values) throws Exception {
			
			Object text = this.execute(values).pop(Object.class);
			if(text != null) {
				return text.toString();
			} else {
				return "null";
			}
		}
	}
	
	/**
	 * 일반 문자열 처리를 위한 텍스트 요소 클래스
	 * 
	 * @author jmsohn
	 */
	private static class StringElement implements TextElement {
		
		/** 문자열 */
		private String textStr;
		
		/**
		 * 생성자
		 * 
		 * @param textStr 텍스트 문자열
		 * @param isEscape 텍스트 문자열 escape 여부
		 */
		StringElement(String textStr, boolean isEscape) {
			this.textStr = textStr;
		}

		@Override
		public String getText(Map<String, ?> values) throws Exception {
			return this.textStr;
		}
	}
	
	/** 텍스트 요소 목록 변수 - parse 메소드에 의해 생성됨 */
	private List<TextElement> elements;
	
	/**
	 * 생성자
	 * 
	 * @param formatText 표현식을 포함한 텍스트 형식 문자열
	 * @param isEscape 텍스트 문자열 escape 여부
	 */
	private TextGen(String formatText, boolean isEscape) throws Exception {
		this.parse(formatText, isEscape);
	}
	
	/**
	 * 주어진 텍스트 형식 문자열을 컴파일하여 TextGen 객체를 생성하고 반환
	 * 
	 * @param formatText 표현식을 포함한 텍스트 형식 문자열
	 * @param isEscape 텍스트 문자열 escape 여부
	 * @return 생성된 TextGen 객체
	 */
	public static TextGen compile(String formatText, boolean isEscape) throws Exception {
		return new TextGen(formatText, isEscape);
	}
	
	/**
	 * 주어진 텍스트 형식 문자열을 컴파일하여 TextGen 객체를 생성하고 반환
	 * 
	 * @param formatText 표현식을 포함한 텍스트 형식 문자열
	 * @return 생성된 TextGen 객체
	 */
	public static TextGen compile(String formatText) throws Exception {
		return TextGen.compile(formatText, false);
	}

	
	/**
	 * 주어진 텍스트 형식 문자열을 파싱함<br>
	 * 파싱 결과는 멤버 변수 elements에 저장됨 
	 * 
	 * @param formatText 표현식을 포함한 텍스트 형식 문자열
	 * @param isEscape 텍스트 문자열 escape 여부
	 */
	private void parse(String formatText, boolean isEscape) throws Exception {
		
		// 텍스트 요소 목록 생성
		this.elements = new ArrayList<>();
		
		// ${표현식}의 표현식을 담을 임시 변수
		StringBuilder exp = new StringBuilder();

		// 상태 변수, false: 일반 문자열 상태, true: 표현식 상태
		boolean isExp = false;
		
		// 문자열 시작 인덱스 변수
		int startIndex = 0;
		
		// 한 문자씩 파싱 수행
		int index = 0;
		for (; index < formatText.length(); index++) {

			char ch = formatText.charAt(index);
            
			if (isExp == true) {
				
				// 표현식 상태
				if (ch == '}') {
					// 표현식 종료

					// 표현식을 컴파일하여 저장
					this.elements.add(ExpElement.compile(exp.toString()));
                        
					// 표현식 버퍼 초기화
					exp.setLength(0);

					// 일반 문자열 상태로 상태 변경 및 시작 인덱스 설정 
					isExp = false;
					startIndex = index + 1;
                    
				} else {
					
					// 표현식에 추가
					exp.append(ch);
				}

			} else {
            	
				// 일반 문자열 상태
				if (ch == '$' && formatText.charAt(index+1) == '{' && index+1 < formatText.length()) {
					
					// "${" 가 들어온 경우
					// 현재까지 문자열을 StringElement로 만들어 추가
					if(startIndex < index) {
						
						this.elements.add(
							new StringElement(
								formatText.substring(startIndex, index),
								isEscape
							)
						);
						
						startIndex = index;
					}
					
					// 표현식 상태로 변환
					isExp = true;
					index++;
				}
			}
		} // End of for
		
		// 모든 문자열을 파싱을 했는데 문자열이 남아 있는 경우 추가함
		if(startIndex < index) {
			
			this.elements.add(
				new StringElement(
					formatText.substring(startIndex, index),
					isEscape
				)
			);
		}
	}
	
	/**
	 * 이미 파싱된 텍스트 형식 문자열(elements)와 변수 목록을 사용하여 문자열을 생성하고 반환함
	 * 
	 * @param values 변수 목록
	 * @return 생성된 문자열
	 */
	public String gen(Map<String, ?> values) throws Exception {
		
		StringBuilder text = new StringBuilder("");
		
		for(TextElement element: this.elements) {
			text.append(element.getText(values));
		}
		
		return text.toString();
	}
}
