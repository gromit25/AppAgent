package com.redeye.babe.config;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 설정 값 관리 클래스
 * 설정 파일(babe.agent.conf property 설정된 파일)에서 읽어온다.
 * @author jmsohn
 */
public enum RootConfig {
	
	/** agent의 패키지명 */
	AGENT_PACKAGE("agent.package"),
	/**
	 * system process id
	 * config 파일에서 읽어오지 않는다.
	 */
	SYSTEM_PID("system.pid") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			// do nothing
			// 값을 설정하지 못하도록 한다.
		}
	},
	/**
	 * system server name
	 * config 파일에서 읽어오지 않는다.
	 */
	SYSTEM_SERVERNAME("system.servername") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			// do nothing
			// 값을 설정하지 못하도록 한다.
		}		
	},
	/** system 구분자(id) */
	SYSTEM_ID("system.id"),
	/** CHARACTER SET */
	SYSTEM_CHARSET("system.charset") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			this.valueObject = Charset.forName(value);
			this.value = value;
		}
		
	},
	/** 무결성 검사 파일명 */
	INTIGRITY_CHECK_FILE("intigrity.checkfile") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			this.valueObject = new File(value);
			this.value = value;
		}
		
	},
	/** 변환 맵(TransformMap) 설정 파일명 */
	TRANSFORM_FILE("transform.file") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			this.valueObject  = new File(value);
			this.value = value;
		}
		
	},
	/** 로그를 남길 파일명 형식 */
	LOG_FILE_PATH("log.file.path") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			
			if(value == null || value.trim().length() == 0) {
				throw new Exception(this.getPropertyName() + " is not set.");
			}
			
			this.value = value;
			
			// 파일명의 형식이기 때문에 File 객체로 만들 의미가 없음
			// LogFileManager에서 파일명을 해석해서 사용
			this.valueObject = value;
		}
	},
	/** 로그 파일의 최대치 (LogFileManager에서 사용)*/
	LOG_FILE_MAXSIZE("log.file.maxsize") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			
			// 설정된 최대치를 파싱한다.
			// ex) 10, 10K, 10M, 10G 등
			Pattern sizeP = Pattern.compile("(?<size>[0-9]+)[ \\t]*(?<unit>[GMK])?");
			Matcher sizeM = sizeP.matcher(value);
			
			if(sizeM.matches() == false) {
				throw new Exception(this.getPropertyName() + " is not valid format : " + value);
			}
			
			long size = Long.parseLong(sizeM.group("size"));
			String unit = sizeM.group("unit");
			
			// 설정된 단위(unit)에 따라 size를 보정한다.
			if(unit != null) {
				 
				if(unit.equals("K")) {
					// Kilo byte
					size *= 1024;
				} else if(unit.equals("M")) {
					// Mega byte
					size *= 1024*1024;
				} else if(unit.equals("G")) {
					// Giga byte
					size *= 1024*1024*1024;
				}
			}
			
			// 보정된 값으로 설정 한다.
			this.value = Long.toString(size);
			this.valueObject = size;
		}
	},
	/** 로그파일검사 주기 */
	LOG_FILE_INSPECTIONPERIOD("log.file.inspectionperiod") {
		
		@Override
		protected void setValue(final String value) throws Exception {

			this.value = value;
			this.valueObject = Long.parseLong(value); 
		}
	},
	/** 스택 트레이스 정보를 남길 package 명*/
	LOG_TRACE_PACKAGE("log.tracepackage") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			
			HashSet<String> loggingPackages = new HashSet<String>();
			
			Stream
				.of(value.split("[ \\t\\r\\n]+"))
				.forEach(
					loggingPackage -> {
						loggingPackages.add(loggingPackage.trim());
					}
				);
			
			this.value = value;
			this.valueObject = loggingPackages;
		}
	},
	LOG_SHORT_PACKAGE_YN("log.shortpackage.yn") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			
			this.value = value;
			
			if(value != null && value.equalsIgnoreCase("y")) {
				this.valueObject = Boolean.TRUE;
			} else {
				this.valueObject = Boolean.FALSE;
			}
		}
	},
	LOG_WRITER_INITIAL_CNT("log.writer.initcount") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			this.value = value;
			this.valueObject = Integer.parseInt(value); 
		}
	},
	LOG_WRITER_PROCESSING_CNT("log.writer.processingcount") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			this.value = value;
			this.valueObject = Integer.parseInt(value); 
		}
	},
	LOG_WRITER_MONITORING_PERIOD("log.writer.monitoringperiod") {
		
		@Override
		protected void setValue(final String value) throws Exception {

			this.value = value;
			this.valueObject = Long.parseLong(value); 
		}
	},
	LOG_WRITER_MAX_SURPLUS_TIME("log.writer.maxsurplustime") {
		
		@Override
		protected void setValue(final String value) throws Exception {

			this.value = value;
			this.valueObject = Long.parseLong(value); 
		}
	},
	LOG_WRITER_MAX_TX_CNT("log.writer.maxtxcount") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			this.value = value;
			this.valueObject = Integer.parseInt(value); 
		}
	},
	LOG_WRITER_MAX_CNT("log.writer.maxcount") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			
			this.value = value;
			this.valueObject = Integer.parseInt(value); 
		}
	},
	/** DB 접근 로그 남길때, 설정에 포함된 것만 로깅 */
	DB_SQL_INCLUDE("db.sql.include") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			
			this.value = value;
			
			if(value != null) {
				
				HashSet<String> includes = new HashSet<String>();
				String[] splitedIncludes = value.split("[ \t]+");
				
				for(String include : splitedIncludes) {
					includes.add(include);
				}
				
				this.valueObject = includes;
			}
		}
	}, 
	/** DB 접근 로그 남길때, 설정에 포함된 것은 로깅하지 않음 */
	DB_SQL_EXCLUDE("db.sql.exclude") {
		
		@Override
		protected void setValue(final String value) throws Exception {
			
			this.value = value;
			
			if(value != null) {
				
				HashSet<String> excludes = new HashSet<String>();
				String[] splitedExcludes = value.split("[ \t]+");
				
				for(String exclude : splitedExcludes) {
					excludes.add(exclude);
				}
				
				this.valueObject = excludes;
			}
		}
	};
	
	//---------------------------
	/** ConfigValue 파일 변경 여부 감시용 Watcher */
	private static RootConfigFileWatcher watcher;
	/** */
	private static boolean isValid;
	
	/**
	 * ConfigValue 클래스 로딩시,
	 * 설정 초기화를 수행한다.
	 */
	static {
		try {
			
			RootConfig.setValid(false);
			
			// PID와 현재 서버명을 설정한다.
			// runtime = PID@현재서버명 형태로 되어 있음
			String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
			
			RootConfig.SYSTEM_PID.value = "NA";
			RootConfig.SYSTEM_PID.valueObject = "NA";
				
			RootConfig.SYSTEM_SERVERNAME.value = "NA";
			RootConfig.SYSTEM_SERVERNAME.valueObject = "NA";
			
			if(runtimeName != null) {
				
				String[] splitedRuntimeName = runtimeName.split("@");
				
				if(splitedRuntimeName.length > 1) {
					
					RootConfig.SYSTEM_PID.value = splitedRuntimeName[0];
					RootConfig.SYSTEM_PID.valueObject = splitedRuntimeName[0];
						
					RootConfig.SYSTEM_SERVERNAME.value = splitedRuntimeName[1];
					RootConfig.SYSTEM_SERVERNAME.valueObject = splitedRuntimeName[1];
				}
			}
			
			// babe.agent.conf property에 설정된 파일명에 
			// 설정파일을 읽어온다.
			String confFileName = System.getProperty("babe.agent.conf");
			RootConfig.watcher = new RootConfigFileWatcher(confFileName);
			RootConfig.watcher.start();
			
			RootConfig.setValid(true);

		} catch(Exception ex) {
			// TODO
			RootConfig.setValid(false);
			ex.printStackTrace();
		}
	}
	
	public static boolean isValid() {
		return RootConfig.isValid;
	}

	public static void setValid(boolean isValid) {
		RootConfig.isValid = isValid;
	}
	
	//---------------------------
	/** 속성(property) 명 */
	protected String propertyName;
	/** 속성값 */
	protected String value;
	/** 속성에 설정된 값을 객체화 하여 저장 */
	protected Object valueObject;
	
	/**
	 * 속성 생성자
	 * @param propertyName 속성명
	 */
	private RootConfig(final String propertyName) {
		this.setPropertyName(propertyName);
	}
	
	/**
	 * 설정값이 사용가능한 상태인지 여부
	 * @return 설정값이 사용가능한 상태인지 여부
	 */
	public boolean isAvailable() {
		
		if(this.value == null || this.valueObject == null) {
			return false;
		}
		
		if(this.value.trim().equals("")) {
			return false;
		}
		
		return true;
	}

	/**
	 * 속성명(property name)
	 * @return 속성명(property name)
	 */
	public String getPropertyName() {
		return this.propertyName;
	}

	/**
	 * 속성명(property name) 설정
	 * @param propertyName 속성명(property name)
	 */
	private void setPropertyName(final String propertyName) {
		this.propertyName = propertyName;
	}
	
	/**
	 * 속성값
	 * @return 속성값
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * 속성값 설정
	 * @param value 속성값
	 */
	protected void setValue(final String value) throws Exception {
		this.value = value;
		this.valueObject = value;
	}
	
	/**
	 * 속성에 설정된 값을 객체되어 저장된 값 
	 * @param type 객체의 class type
	 * @return 속성에 설정된 값을 객체되어 저장된 값
	 */
	public <T> T getValueObject(final Class<T> type) {
		
		if(type.isInstance(this.valueObject) == true) {
			return type.cast(this.valueObject);
		}
		
		return null;
	}

}
