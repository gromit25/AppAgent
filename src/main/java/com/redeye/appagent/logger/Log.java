package com.redeye.appagent.logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.redeye.appagent.Config;
import com.redeye.appagent.builtins.ContentsApp;

/**
 * 로깅 클래스
 * 
 * @author jmsohn
 */
public class Log {
	
	/** Logger 에게 로그메시지를 전달할 큐 객체 */
	private static BlockingQueue<String> outQ;
	
	/** Logger 목록 - Logger: 실제 로그를 저장 작업 수행 */
	private static List<Logger> loggers;
	
	/** 큐의 최대 로그 메시지 개수 */
	private static int maxLogCount;
	
	/** 스택 트레이스 정보를 남길 package 목록 */
	private static Set<String> tracePackages;
	
	/** 패키지 명 줄임 여부 */
	private static boolean isShortPackage; 
	
	static {
		
		// --- 로깅 객체 생성 ---
		
		// LogWriter를 생성할 빌더 객체 생성
		LogWriterBuilder writerBuilder = new LogWriterBuilder(Config.LOG_TYPE.getValue());
		
		// Logger 목록 객체 생성
		loggers = new ArrayList<>();

		// 주어진 개수 만큼 Logger 생성 및 스레드 수행
		int loggerCount = 5;
		
		try {
			
			loggerCount = Integer.parseInt(Config.LOG_WRITER_COUNT.getValue());
			if(loggerCount <= 0) {
				throw new Exception();
			}
			
		} catch(Exception ex) {
			
			System.out.println("Invalid Value(LOG_WRITER_COUNT):" + Config.LOG_WRITER_COUNT.getValue());
			System.out.println("set default LOG_WRITER_COUNT = 5");
			
			loggerCount = 5;
		}
		
		for(int index = 0; index < loggerCount; index++) {
			
			// Logger 객체 생성
			Logger logger = new Logger(outQ, writerBuilder.create());
			
			// Logger 스레드 생성 및 수행
			Thread loggerThread = new Thread(logger);
			loggerThread.setDaemon(true);	// 메인 프로그램 종료시 스레드 종료 설정
			loggerThread.start();
			
			// Logger 목록에 추가
			loggers.add(logger);
		}
		
		// 로그 큐의 최대 개수 설정
		try {
			
			maxLogCount = Integer.parseInt(Config.LOG_MAX_QUEUE_COUNT.getValue());
			if(maxLogCount <= 0) {
				throw new Exception();
			}
			
		} catch(Exception ex) {
			
			System.out.println("Invalid Value(LOG_MAX_QUEUE_COUNT):" + Config.LOG_MAX_QUEUE_COUNT.getValue());
			System.out.println("set default LOG_MAX_QUEUE_COUNT = 1000");
			
			maxLogCount = 1000;
		}
		
		// --- 로그 메시지 생성 관련 설정 --- 
		
		// 스택 트레이스 정보를 남길 package 목록 초기화
		tracePackages = new HashSet<>();
		String[] tracePackageAry = Config.LOG_TRACE_PACKAGES.getValue().split("[ \\t]*,[ \\t]*");
		
		for(String tracePackage: tracePackageAry) {
			tracePackages.add(tracePackage);
		}
		
		// 패키지 명 줄임 여부 설정
		if(Config.LOG_SHORT_PACKAGE_YN.getValue().equals("Y") == true) {
			isShortPackage = true;
		} else {
			isShortPackage = false;
		}
	}
	
	/**
	 * Agent 자체 로그 저장
	 * 
	 * @param logFormat 로그 형식
	 * @param params 로그 파라미터
	 */
	public static void writeAgentLog(String logFormat, Object... params) {
		
		writeLog(
			"AGENT", "AGENT",
			(long)0,
			logFormat, params
		);
	}
	
	/**
	 * 로그 저장
	 * 
	 * @param apiType 호출 API 종류
	 * @param obj 호출 객체
	 * @param logFormat 로그 형식
	 * @param params 로그 파라미터
	 */
	public static void writeLog(
		String apiType,
		Object obj,
		String logFormat,
		Object... params
	) {
		
		writeLog(
			apiType, obj,
			(long)0,
			logFormat, params
		);
	}

	/**
	 * 로그 저장
	 * 
	 * @param apiType 호출 API 종류
	 * @param obj 호출 객체
	 * @param elapsedTime 호출 시간 - 만일 메소드 호출 전 이라면 0
	 * @param logFormat 로그 형식
	 * @param params 로그 파라미터
	 */
	public static void writeLog(
		String apiType,
		Object obj,
		long elapsedTime,
		String logFormat,
		Object... params
	) {

		// 입력값 검사
		if(logFormat == null) return;
		
		// Logger 가 없는 경우 로그를 남기지 않고 반환
		if(loggers == null || loggers.size() == 0) return;

		try {
			
			// 메시지 생성
			String logMsg = genLogMsg(apiType, obj, elapsedTime, logFormat, params);
			
			// 만일 큐가 정해진 숫자 이상이면
			// 큐에 로그를 추가하지 않음
			if(outQ.size() > maxLogCount) {
				return;
			}
			
			// 큐에 메시지 추가
			outQ.put(logMsg);
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 로그 메시지 생성 후 반환
	 * 
	 * @param apiType 호출 API 종류
	 * @param obj 호출 객체
	 * @param elapsedTime 호출 시간 - 만일 메소드 호출 전 이라면 0
	 * @param logFormat 로그 형식
	 * @param params 로그 파라미터
	 * @return 생성된 로그 메시지
	 */
	private static String genLogMsg(
		String apiType,
		Object obj,
		long elapsedTime,
		String logFormat,
		Object[] params
	) {
		
		// 현재시간을 가져옴
		long curTime = System.currentTimeMillis();
		
		// 트랜잭션 ID가 없으면 만듦
		if(ContentsApp.getTxId() == null) {
			String newTxId = "TX_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
			ContentsApp.setTxId(newTxId);
		}
		
		// 호출API별메시지 조립
		String log = logFormat;
		if(params != null && params.length > 0) {
			log = String.format(log, params);
		}
		
		// 객체식별자를 만듦
		// 객체식별자 = 객체해시값@클래스명
		// 클래스명은 패키지명을 포함하지 않음
		// 만일 객체가 스트링 타입일 경우 그냥 그대로 사용 : STATIC, CREATE 등의 경우
		// 만일 객체가 null 일 경우 NA로 표시
		// 만일 클래스가 Agent의 Class일 경우(ex. Wrapper), Agent 클래스가 아닌 상위 클래스를 표기
		//    최상위까지 왔는데 클래스가 없는 경우 NONE으로 표시
		String objId = "N/A";
		if(obj != null) {
			
			if(obj instanceof String) {
				objId = obj.toString();
			} else {
				
				String agentPackageName = Config.AGENT_PACKAGE.getValue();
				
				String className = "NONE";
				Class<?> curClass = obj.getClass(); 
				while(curClass != null) {
					
					if(curClass.getName().startsWith(agentPackageName) == false) {
						className = curClass.getSimpleName();
						break;
					}
					curClass = curClass.getSuperclass();
				}
				
				objId = obj.hashCode() + "@" + className;
			}
		}
		
		// 스택트레이스를 가져옴
		String stackTraceMsg = makeStackTraceMsg(Thread.currentThread());
		
		// 추가 정보를 이용해 로그 생성
		// format)
		// [시간(long값)]\t[수행시간(ms)]\t[프로세스ID]\t[트랜잭션ID]\t[호출API종류]\t[객체식별자]\t[스택트레이스]\t[[호출API별메시지]\u001E]
		StringBuilder logBuilder = new StringBuilder("");
		logBuilder
			.append(curTime)				//[시간(long값)]
			.append("\t")
			.append(elapsedTime)			//[수행시간(ms)]
			.append("\t")
			.append(Config.SYSTEM_PID.getValue())	//[프로세스ID]
			.append("\t")
			.append(ContentsApp.getTxId())	//[트랜잭션ID]
			.append("\t")
			.append(apiType)				//[호출API종류]
			.append("\t")
			.append(objId)					//[객체hash값]
			.append("\t")
			.append(log)					//[[호출API별메시지]]
			.append("\t")
			.append(stackTraceMsg)			//[스택트레이스] <-- 임시로 뒤로뺌
			.append("\u001E")				//[ASCII 코드 RS(Record Separator)]
			.append("\r\n");
		
		return logBuilder.toString();
	}
	
	/**
	 * 주어진 스레드의 스택 트레이스 메시지 생성 및 반환
	 * 
	 * @param t 스택 트레이스 메시지를 생성할 스레드
	 * @return 생성된 스택 트레이스 메시지 
	 */
	private static String makeStackTraceMsg(Thread t) {
		
		// 트레이싱 정보를 추가함
		// 트레이싱 패키지가 없을 경우 즉시 공백 문자 반환
		if(tracePackages.size() == 0) return "";
		
		StringBuilder stackBuilder = new StringBuilder("");
		StackTraceElement[] stacks = t.getStackTrace();
		
		// 스택 목록의 스택 정보를 하나씩 추가함
		for(StackTraceElement stack : stacks) {
			
			for(String tracePackage : tracePackages) {

				// stack의 클래스 전체 이름이
				// 설정된 trace package의 이름으로 시작되거나,
				// LOG_TRACE_PACKAGES 설정값이 "*" 이면 트레이싱 정보 추가
				if(stack.getClassName().startsWith(tracePackage) == true
					|| Config.LOG_TRACE_PACKAGES.getValue().equals("*") == true) {
					
					// 이전 스택 정보가 있으면,
					// 꺽쇠(">") 추가
					if(stackBuilder.length() != 0) {
						stackBuilder.append(">");
					}
					
					// 클래스명 획득
					String className = stack.getClassName();
					
					// 패키지명 축약 설정되어 있으면, 클래스명의 패키지명을 축약형으로 만듦
					// ex) com.redeye.Test -> c.r.Test
					if(isShortPackage == true) {
						
						String[] packageNames = className.split("\\.");
						StringBuilder classNameBuilder = new StringBuilder("");
						
						for(int index = 0; index < packageNames.length - 1; index++) {
							
							String packageName = packageNames[index];
							classNameBuilder.append(packageName.charAt(0)).append(".");
						}
						
						classNameBuilder.append(packageNames[packageNames.length - 1]);
						
						className = classNameBuilder.toString();
					}
					
					// 스택 클래스명과 메소드 명, 라인 수 추가
					stackBuilder.append(className)
						.append(".")
						.append(stack.getMethodName())
						.append(":")
						.append(stack.getLineNumber());
					
					break;
				}
			}
		}
		
		return stackBuilder.toString();
	}
}
