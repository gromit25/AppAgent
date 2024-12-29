package com.redeye.appagent.script.parserfw;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Hashtable;

import com.redeye.appagent.script.parserfw.exception.ParseException;
import com.redeye.appagent.script.parserfw.exception.UnexpectedEndException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 문자열 파싱하는 추상 클래스
 * -> 상태 변환 내용 및 변환 이벤트 처리는 하위 클래스에서 처리함
 *    본 클래스에서는 문자열을 읽고,
 *    상태 변환 내용 및 상태 변환시 이벤트 핸들링 메소드를 호출함 
 * 
 * @author jmsohn
 */
public abstract class AbstractParser<T> {

	/** 파싱 상태 변수 */
	@Getter(value = AccessLevel.PROTECTED)
	private String status;
	
	/** 파싱 트리의 루트 노드 */
	@Setter(value = AccessLevel.PROTECTED)
	private TreeNode<T> node = new TreeNode<T>();
	
	/**
	 * 상태 변환 정보 목록<br>
	 * -> ex) "A" 상태에서 문자 "B"가 들어오면 "C" 상태로 변한다는 정보
	 */
	private Hashtable<String, ArrayList<Transfer>> transferMap = new Hashtable<String, ArrayList<Transfer>>();
	
	/** 상태 변환시, 수행되는 전이함수(transfer function) 목록 */
	private Hashtable<String, Hashtable<String, ArrayList<Method>>> transferHandlers = new Hashtable<String, Hashtable<String, ArrayList<Method>>>();
	
	/**
	 * 종료 상태 목록 - Key: 종료 상태명, Value: 종료 상태 종류<br>
	 * 종료 상태 종류 : 0 - 일반 종료 상태, 1 - 종료 상태에 들어올 경우 Parsing도 종료
	 */
	private Hashtable<String, EndStatusType> endStatus = new Hashtable<String, EndStatusType>();

	/**
	 * 생성자
	 */
	public AbstractParser() throws Exception {
		
		// 하위 클래스에서 구현된 transfer event handler 메소드를 목록에 등록함
		// 현재 클래스의 메소드 목록을 가져옴
		Method[] methods = this.getClass().getMethods();
		
		// 각 메소드 별로 TransferEventHandler Annotation이 있는지 확인하여 등록함
		// 
		for(Method method: methods) {
			
			// 메소드의 TransferEventHandler Annotation 정보를 가져옴
			// 없을 경우, 다음 메소드 검사
			TransferEventHandler handlerAnnotation = method.getAnnotation(TransferEventHandler.class);
			if(handlerAnnotation == null) {
				continue;
			}
			
			// 전이 시작 상태 목록 변수
			String[] sources = handlerAnnotation.source();
			// 전이 종료 상태 목록 변수
			String[] targets = handlerAnnotation.target();
			
			// 전이 시작 및 종료 상태에 따른 TransferEventHandler 등록
			// "시작" -> "종료" , method1 형태로 저장 
			for(String source: sources) {
			
				// 시작 상태가 등록되어 있지 않으면, 등록 수행
				if(this.transferHandlers.containsKey(source) == false) {
					this.transferHandlers.put(source, new Hashtable<String, ArrayList<Method>>());
				}
				
				Hashtable<String, ArrayList<Method>> sourceMap = this.transferHandlers.get(source);
				
				// 종료 상태와 TransferEventHandler 메소드를 등록함
				for(String target: targets) {
					
					if(sourceMap.containsKey(target) == false) {
						sourceMap.put(target, new ArrayList<Method>());
					}
					
					ArrayList<Method> handlers = sourceMap.get(target);
					
					// TransferEventHandler의 파라미터 개수는 1개여야 함
					if(method.getParameterCount() != 1) {
						throw new Exception("parameter count is not 1");
					}
					
					Parameter[] params = method.getParameters();
					if(params[0].getType() != Event.class) {
						throw new Exception("parameter is not Event type:" + params[0].getType());
					}
					
					handlers.add(method);
					
				} // End of for targets
				
			} // End of for sources
			
		} // End of for methods
	}
	
	/**
	 * 시작 상태 반환
	 * 
	 * @return 시작 상태
	 */
	protected abstract String getStartStatus();
	
	/**
	 * 파싱 처리 시작 전 콜백
	 */
	protected abstract void init() throws Exception;
	
	/**
	 * 파싱 종료시 콜백
	 */
	protected void exit() throws Exception {
		// Do Nothing
		// 하위 클래스에서 필요시 구현
	}
	
	/**
	 * 상태 전이 맵 추가
	 * 
	 * @param startStatus 변경전 상태명
	 * @param toStatusMap 상태 전이 맵 목록 
	 */
	protected void putTransferMap(String startStatus, ArrayList<Transfer> toStatusMap) {
		
		if(startStatus == null) {
			throw new NullPointerException("start status is null");
		}
		
		if(toStatusMap == null) {
			throw new NullPointerException("to status map is null");
		}
		
		this.transferMap.put(startStatus, toStatusMap);
	}
	
	/**
	 * 종료 상태 추가
	 * 
	 * @param endStatus 종료 상태 명
	 * @param type 종료 상태 종류
	 */
	protected void putEndStatus(String endStatus, EndStatusType type) {
		this.endStatus.put(endStatus, type);
	}

	/**
	 * 종료 상태 추가
	 * 
	 * @param endStatus 종료 상태 명
	 */
	protected void putEndStatus(String endStatus) {
		this.putEndStatus(endStatus, EndStatusType.NORMAL_END);
	}
	
	/**
	 * 주어진 시작 상태와 종료 상태에 수행되어야 하는 TransferEventHandler 메소드 목록을 반환
	 * 
	 * @param source 시작 상태
	 * @param target 종료 상태
	 * @return TransferEventHandler 메소드 목록
	 */
	private ArrayList<Method> getHandlers(String source, String target) throws Exception {
		
		// 핸들러 목록에 소스가 없는 경우, 빈 array 반환
		if(this.transferHandlers.containsKey(source) == false) {
			return new ArrayList<Method>();
		}
		
		Hashtable<String, ArrayList<Method>> sourceMap = this.transferHandlers.get(source);
		
		// 소스 핸들러 목록에 타깃이 없는 경우, 빈 array 반환
		if(sourceMap.containsKey(target) == false) {
			return new ArrayList<Method>();
		}
		
		return sourceMap.get(target);
	}
	
	/**
	 * 문자열(script)를 입력받아 파싱 수행 후 루트 노드 반환 
	 * 
	 * @param script 파싱할 문자열
	 * @return 파싱 트리의 루트 노드
	 */
	public TreeNode<T> parse(String script) throws ParseException, Exception {
		
		ExpReader in = new ExpReader(new StringReader(script), 1024);
		return this.parse(in);
		
	}
	
	/**
	 * 문장을 한 글자씩 파싱한 후 파싱 트리의 루트 노드를 반환
	 * 
	 * @param in 파싱할 문장의 Reader
	 * @return 파싱 트리의 루트 노드
	 */
	public TreeNode<T> parse(ExpReader in) throws ParseException, Exception {
		
		// 최초 시작시 실행
		this.init();
		
		// 시작 상태로 상태 초기화
		this.status = this.getStartStatus();
		
		// 시작 상태가 전이함수 목록에 없는 경우 예외 발생 
		if(this.transferMap.containsKey(this.status) == false) {
			throw new Exception("invalid status: " + this.status);
		}
		
		// pushback을 수행할 문자열 저장 버퍼 변수
		CharBuffer pushbackBuffer = CharBuffer.allocate(1024);
		
		// Reader에서 한문자씩 읽어들여 상태를 전환하고,
		// 각 상태 전환에 따른 전이함수(transfer function)을 실행시킴 
		int read = in.read();
		
		while(read != -1) {
			
			// 입력 문자 변수
			char ch = (char)read;
			pushbackBuffer.append(ch);
			
			// 유효한 전이함수(현재 상태에서 입력 문자가 있는 경우)가 있는지 여부 변수
			boolean isMatched = false;
			
			// 전이 함수 목록에서 유효한 전이 함수가 있는지 확인함
			// 유효한 전이 함수에 따라 상태 변화 후 상태 변화에 따른 TransferEventHandler 메소드를 수행함
			ArrayList<Transfer> transferFunctions = this.transferMap.get(this.status);
			if(transferFunctions == null) {
				throw new Exception("Transfer Function is not found: " + this.status + "-> ???");
			}
			
			for(Transfer transferFunction: transferFunctions) {
				if(transferFunction.isValid(ch) == true) {
					
					// 유효한 전이함수(transfer function)이 매치되었을 경우 true로 설정함
					isMatched = true;
					
					// pushback 수행
					if(transferFunction.getPushback() < 0) {
						
						// 읽기 모드 전환
						pushbackBuffer.flip();
						
						// pushback할 크기를 가져옴
						// pushback 크기가 버퍼의 크기보다 크면,
						// pushback 크기를 버퍼의 크기로 맞춤
						int pushbackSize = 0;
						if(pushbackSize != Integer.MIN_VALUE) {
							
							// 주의) Integer.MIN_VALUE * -1은 Integer.MIN_VALUE(-2147483648)가 나옴
							pushbackSize = transferFunction.getPushback() * -1;
							if(pushbackBuffer.remaining() - pushbackSize < 0) {
								pushbackSize = pushbackBuffer.remaining();
							}
							
						} else {
							pushbackSize = pushbackBuffer.remaining();
						}
						
						// pushback할 배열 생성
						char[] unread = new char[pushbackSize];
						copyBuffer(pushbackBuffer
							, unread
							, pushbackBuffer.remaining() - pushbackSize
							, pushbackSize 
						);
						
						// pushback 수행
 						in.unread(unread);
 						
						pushbackBuffer.clear();
					}
					
					// 다음 상태명 변수
 					String nextStatus = transferFunction.getNextStatus();
 					
					// 이벤트 생성
					Event event = new Event(ch, in, this.status, nextStatus);

					// 이벤트 처리함수 호출
					ArrayList<Method> handlers = this.getHandlers(this.status, nextStatus);
					for(Method handler: handlers) {
						handler.invoke(this, event);
					}
					
					// 다음 상태로 상태를 변경
					this.status = nextStatus;
					
					// for문 종료 -> 다른 전이함수는 검사하지 않음
					break;
				}
			}
			
			// 매치되는 전이함수가 없을 경우 예외 발생
			if(isMatched == false) {
				throw new ParseException(in.getPos(), ch, this.status);
			}
			
			// 상태가 ERROR 상태일 경우 예외 발생 시킴
			if(this.endStatus.containsKey(this.status) == true
				&& this.endStatus.get(this.status) == EndStatusType.ERROR) {
				throw new ParseException(in.getPos(), ch, this.status);
			}
			
			// 종료 상태의 종류가 IMMEDIATELY_END이면 parsing 종료 처리함
			if(this.endStatus.containsKey(this.status) == true
				&& this.endStatus.get(this.status) == EndStatusType.IMMEDIATELY_END) {
				
				break;
			}
			
			// 다음 글자를 읽어옴
			read = in.read();
			
		} // End of while
		
		// parsing 종료가 되었으나 종료 상태가 아닌 경우 예외 발생
		if(this.endStatus.containsKey(this.status) == false) {
			throw new UnexpectedEndException(in.getPos(), this.status);
		}
		
		// 파싱 종료 시 호출
		this.exit();
		
		// 생성된 파싱 트리를 반환
		return this.node;
		
	} // End of parse
	
	/**
	 * CharBuffer의 내용을 배열(dst)에 복사하는 메소드
	 * 
	 * @param src 복사할 CharBuffer
	 * @param dst 복사 대상 배열
	 * @param index CharBuffer의 시작 위치
	 * @param length 복사할 크기
	 */
	private static void copyBuffer(CharBuffer src, char[] dst, int index, int length) {
		 for (int i = 0, j = index; i < length; i++, j++) {
	         dst[i] = src.get(j);
		 }
	}
	
	/**
	 * 현재 노드에 데이터를 설정함
	 * 
	 * @param data 설정할 데이터
	 */
	protected void setNodeData(T data) {
		this.node.setData(data);
	}
	
	/**
	 * 현재 노드에 자식 노드를 추가함
	 * 
	 * @param childNode 추가할 자식 노드
	 */
	protected void addChild(TreeNode<T> childNode) {
		this.node.addChild(childNode);
	}
	
	/**
	 * 현재 노드에 자식 노드를 추가함
	 * 
	 * @param index 추가할 자식 노드의 인덱스
	 * @param childNode 추가할 자식 노드
	 */
	protected void addChild(int index, TreeNode<T> childNode) {
		this.node.addChild(index, childNode);
	}
	
	/**
	 * 현재 노드에 자식 노드를 추가함
	 * 
	 * @param childData
	 */
	protected void addChildData(T childData) {
		this.node.addChild(new TreeNode<T>(childData));
	}
	
	/**
	 * 상태 전환시 발생 Event 클래스
	 * 
	 * @author jmsohn
	 */
	protected static class Event {
		
		/** Event 발생시 입력된 문자 */
		@Getter
		private char ch;
		
		/** 입력 스트림 */
		@Getter
		private ExpReader reader;
		
		/** source 상태명 */
		@Getter
		private String source;
		
		/** target 상태명 */
		@Getter
		private String target;
		
		/**
		 * 생성자
		 * 
		 * @param ch Event 발생시 입력된 문자
		 * @param reader 입력 스트림
		 * @param source source 상태명
		 * @param target target 상태명
		 */
		public Event(char ch, ExpReader reader, String source, String target) {
			
			this.ch = ch;
			this.reader = reader;
			this.source = source;
			this.target = target;
		}
	}
}
