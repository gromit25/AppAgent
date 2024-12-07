package com.redeye.babe.log;

import java.util.HashSet;
import java.util.Set;

import com.redeye.babe.config.Config;
import com.redeye.babe.wrapper.ContentsApp;

/**
 * 로깅 클래스
 * 
 * @author jmsohn
 */
public class Log {
	
	/** */
	private static LogWriter writer;

	/** 스택 트레이스 정보를 남길 package 목록 */
	private static Set<String> tracePackages;
	
	/** 패키지 명 줄임 여부 */
	private static boolean isShortPackage; 
	
	static {
		
		// TODO writer 객체 생성
		
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
			ApiType.AGENT.getName(), ApiType.AGENT.getName(),
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
			String apiType, Object obj,
			String logFormat, Object... params
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
			String apiType, Object obj,
			long elapsedTime,
			String logFormat, Object... params
	) {

		// 입력값 검사
		if(logFormat == null) return;
		
		// 로그 writer가 설정되어 있지 않은 경우 로그를 남기지 않음
		if(writer == null) return;

		try {

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

			// 로그 저장
			writer.write(logBuilder.toString());
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 주어진 스레드의 스택 트레이스 메시지 생성 및 반환
	 * 
	 * @param t 스택 트레이스 메시지를 생성할 스레드
	 * @return 생성된 스택 트레이스 메시지 
	 */
	private static String makeStackTraceMsg(final Thread t) {
		
		// 트레이싱 정보를 추가함
		// 트레이싱 패키지가 없을 경우 즉시 공백 문자 반환
		if(tracePackages.size() == 0) return "";
		
		StringBuilder stackBuilder = new StringBuilder("");
		StackTraceElement[] stacks = t.getStackTrace();
		
		// 스택 목록의 스택을 하나씩 처리함
		for(StackTraceElement stack : stacks) {
			
			for(String tracePackage : tracePackages) {

				// stack의 클래스 전체 이름이
				// 설정된 trace package의 이름으로 시작되거나,
				// LOG_TRACE_PACKAGES 설정값이 "*" 이면 트레이싱 정보 추가
				if(stack.getClassName().startsWith(tracePackage)
					|| Config.LOG_TRACE_PACKAGES.getValue().equals("*")) {
					
					if(stackBuilder.length() != 0) {
						stackBuilder.append(" > ");
					}
					
					String className = stack.getClassName();
					
					// 패키지명 축약 설정되어 있으면, 클래스명의 패키지명을 축약형으로 만듦
					// ex) com.epozen.Test -> c.e.Test
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
					
					//
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
