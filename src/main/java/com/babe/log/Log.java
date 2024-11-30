package com.babe.log;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.stream.Stream;

import com.babe.config.RootConfig;
import com.babe.wrapper.ContentsApp;

/**
 * 로깅 클래스
 * 
 * @author jmsohn
 */
public class Log {
	
	/**
	 * 데이터베이스 쿼리 수행전 로깅 수행 메소드
	 * 
	 * @param obj 쿼리 수행을 하는 객체(Statement, PreparedStatement, CallableStatement 등)
	 * @param sql 쿼리
	 * @param hash 쿼리의 hash값
	 * @param params 쿼리의 bind 변수 목록
	 */
	public static void writePreSqlLog(final Object obj, final String sql, final String hash
		, final Hashtable<Integer, String> params) {
		
		try {
			
			if(Log.isSqlLogging(sql) == true) {
				
				// params 데이터를 스트링으로 변환한다.
				StringBuilder paramsBuilder = new StringBuilder("");
				
				if(params != null) {
					
					// 변수 순서를 정렬한다.
					Stream.of(params.keySet().toArray())
					.sorted()
					.forEach(key -> {
							
						if(paramsBuilder.length() != 0) {
							paramsBuilder.append(" ");
						}
							
						String formattedParam = String.format("%s=%s", key.toString(), params.get(key));
						paramsBuilder.append(formattedParam);
					});
				}
				
				// 로그를 남긴다.
				Log.writeLog(ApiType.DB.getApiTypeName(), obj, 0
						, "%s\t%s\t%s", hash, sql, paramsBuilder.toString());
			}
			
		} catch(Exception ex) {
			//TODO
			ex.printStackTrace();
		}
	}

	/**
	 * 데이터베이스 쿼리 수행후 로깅 수행 메소드
	 * 
	 * @param obj 쿼리 수행을 하는 객체(Statement, PreparedStatement, CallableStatement 등)
	 * @param sql 쿼리
	 * @param hash 쿼리의 hash값
	 * @param start 쿼리 수행 시작 시간
	 * @param end 쿼리 수행 중지 시간
	 */
	public static void writePostSqlLog(final Object obj, final String sql, final String hash
		, final long start, final long end) {
		
		try {
			
			if(Log.isSqlLogging(sql) == true) {
				
				// 로그를 남긴다.
				Log.writeLog(ApiType.DB.getApiTypeName(), obj, end - start, hash + "\t\t");
			}
			
		} catch(Exception ex) {
			//TODO
			ex.printStackTrace();
		}
	}
	
	/**
	 * 데이터베이스 로깅을 할 쿼리 인지 확인
	 * 
	 * @param sql 검사할 sql문
	 * @return 로깅 여부
	 */
	private static boolean isSqlLogging(String sql) throws Exception {
		
		// sql이 null이면 로그를 남기지 않는다.
		if(sql == null) {
			return false;
		}
		
		// 대소문자를 구분하지 않고 비교하기 위해 toUpperCase를 수행한다. 
		String sqlUpperCase = sql.toUpperCase();
		
		// DB_SQL_EXCLUDE 값이 있고,
		// 쿼리문에서 DB_SQL_EXCLUDE 값이 있는 경우(대소문자 구분안함)
		// 로그를 남기지 않음
		if(RootConfig.DB_SQL_EXCLUDE.isAvailable() == true) {
			
			@SuppressWarnings("unchecked")
			HashSet<String> sqlExcludes = RootConfig.DB_SQL_EXCLUDE.getValueObject(HashSet.class);
			for(String sqlExclude : sqlExcludes) {
				if(sqlUpperCase.contains(sqlExclude) == true) {
					return false;
				}
			}
		}
		
		// DB_SQL_INCLUDE 값이 있고,
		// 쿼리문에서 DB_SQL_INCLUDE 값이 있는 경우(대소문자 구분안함)
		// 로그를 남김
		if(RootConfig.DB_SQL_INCLUDE.isAvailable() == true) {
			
			@SuppressWarnings("unchecked")
			HashSet<String> sqlIncludes = RootConfig.DB_SQL_INCLUDE.getValueObject(HashSet.class);
			for(String sqlInclude : sqlIncludes) {
				if(sqlUpperCase.contains(sqlInclude) == true) {
					return true;
				}
			}
			
			// sql문에 포함된게 없음
			return false;
		}
		
		// 다 통과하면, 로깅 수행
		return true;
	}
	
	/**
	 * Agent 자체에서 발생하는 로그
	 * 
	 * @param logFormat
	 * @param params
	 */
	public static void writeAgentLog(final String logFormat, final Object... params) {
		writeLog(ApiType.AGENT.getApiTypeName(), ApiType.AGENT.getApiTypeName(), (long)0
				, logFormat, params);
	}

	/**
	 * 로그 파일에 로그 저장
	 * 
	 * @param apiType
	 * @param obj
	 * @param elapsedTime
	 * @param logFormat
	 * @param params
	 */
	public static void writeLog(final String apiType, final Object obj
		, final long elapsedTime, final String logFormat, final Object... params) {

		// 파라미터 검사
		if(logFormat == null) return;

		// 현재시간을 가져옴
		long curTime = System.currentTimeMillis();
		
		// 트랜잭션 ID가 없으면 만듦
		if(ContentsApp.getTxId() == null) {
			String newTxId = "TX_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
			ContentsApp.setTxId(newTxId);
		}
		
		// 스택트레이스를 가져옴
		String stackTraceMsg = makeStackTraceMsg(Thread.currentThread());
		
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
		String objId = "NA";
		if(obj != null) {
			
			if(obj instanceof String) {
				objId = obj.toString();
			} else {
				
				String agentPackageName = RootConfig.AGENT_PACKAGE.getValue();
				
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
		
		// TODO
		// 로그에 추가 정보 붙힘
		// format)[시간(long값)]\t[수행시간(ms)]\t[프로세스ID]\t[트랜잭션ID]\t[호출API종류]\t[객체식별자]\t[스택트레이스]\t[[호출API별메시지]]
		StringBuilder logBuilder = new StringBuilder("");
		logBuilder.append("\r\n")
			.append(curTime)				//[시간(long값)]
			.append("\t")
			.append(elapsedTime)			//[수행시간(ms)]
			.append("\t")
			.append(RootConfig.SYSTEM_PID.getValue())	//[프로세스ID]
			.append("\t")
			.append(ContentsApp.getTxId())	//[트랜잭션ID]
			.append("\t")
			.append(apiType)				//[호출API종류]
			.append("\t")
			.append(objId)				//[객체hash값]
			.append("\t")
			.append(log)					//[[호출API별메시지]]
			.append("\t")
			.append(stackTraceMsg);			//[스택트레이스] <-- 임시로 뒤로뺌

		
		// 로그 저장
		try {
			Path sqlFilePath = LogFileManager.getLogFileManager().getLogFile().toPath();
			Files.write(sqlFilePath, logBuilder.toString().getBytes(), StandardOpenOption.APPEND);
		} catch(Exception ex) {
			//TODO
			ex.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	private static String makeStackTraceMsg(final Thread t) {
		
		// 트레이싱 정보를 추가한다.
		if(RootConfig.LOG_TRACE_PACKAGE.getValue() == null) return "";
		
		@SuppressWarnings("unchecked")
		HashSet<String> loggingPackages = RootConfig.LOG_TRACE_PACKAGE.getValueObject(HashSet.class);
		
		StringBuilder stackBuilder = new StringBuilder("");
		StackTraceElement[] stacks = t.getStackTrace();
		
		for(StackTraceElement stack : stacks) {
			
			for(String loggingPackage : loggingPackages) {

				// stack의 클래스 전체 이름이
				// 설정된 logging package의 이름으로 시작되거나,
				// LOG_TRACE_PACKAGE 설정값이 "*" 이면
				// 로그를 남긴다.
				if(stack.getClassName().startsWith(loggingPackage)
					|| RootConfig.LOG_TRACE_PACKAGE.getValue().equals("*")) {
					
					if(stackBuilder.length() != 0) {
						stackBuilder.append(" > ");
					}
					
					String className = stack.getClassName();
					
					// 패키지명 축약 설정되어 있으면, 클래스명의 패키지명을 축약형으로 만든다.
					// ex) com.epozen.Test -> c.e.Test
					if(RootConfig.LOG_SHORT_PACKAGE_YN.getValueObject(Boolean.class) == true) {
						
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
