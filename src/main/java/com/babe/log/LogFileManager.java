package com.babe.log;

import java.io.File;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.babe.config.RootConfig;

/**
 * 로그파일 관리자
 * @author jmsohn
 */
public class LogFileManager extends Thread {
	
	/** 로그파일 관리자 */
	private static LogFileManager logFileManager;
	
	/** 로그 파일 검사 중단 여부 */
	private boolean isStop;
	/** 현재 로그 파일 */
	private File logFile;
	
	//--------------------------------------
	
	static {
		try {
			// 새로운 로그파일을 생성한다.
			LogFileManager.getLogFileManager().makeNewLogFile();;
			
			// 로그 파일 검사 수행을 시작한다.
			LogFileManager.getLogFileManager().start();
			
		} catch(Exception ex) {
			//TODO
			ex.printStackTrace();
		}
	}
	
	/**
	 * 로그파일 관리자를 가져옴(싱글톤)
	 * @return
	 */
	static LogFileManager getLogFileManager() {
		
		if(LogFileManager.logFileManager == null) {
			
			LogFileManager.logFileManager = new LogFileManager();
			
			// main 프로그램 종료시, 같이 종료되도록 Daemon 으로 설정한다.
			LogFileManager.logFileManager.setDaemon(true);
		}
		
		return LogFileManager.logFileManager;
	}
	
	/**
	 * 생성자
	 * 외부에서 생성하지 못하도록 함
	 */
	private LogFileManager() {
	}
	
	@Override
	public void run() {
		
		try {
			
			this.setStop(false);
			
			while(this.isStop() == false) {
				
				// 파일의 크기를 확인한다.
				long logSize = this.getLogFile().length();
				
				// 로그 파일 최대 크기
				long maxSize = RootConfig.LOG_FILE_MAXSIZE.getValueObject(Long.class);
				
				// 파일의 크기가 설정된 MAX값을 초과하거나,
				// 파일이 없는 경우 새로 만듦
				if(logSize > maxSize || this.getLogFile().exists() == false) {
					
					// 새로운 로그파일을 생성한다.
					this.makeNewLogFile();
					
					// 기존 로그 파일 정리한다.
					this.removeOldLogFiles();
				}
				
				// 로그 파일 검사 주기
				long inspectionPeriod = RootConfig.LOG_FILE_INSPECTIONPERIOD.getValueObject(Long.class);
				Thread.sleep(inspectionPeriod);
			}
			
		} catch(Exception ex) {
			//TODO
			ex.printStackTrace();
		}
	}
	
	/**
	 * 새로운 로그파일을 만든다.
	 */
	private void makeNewLogFile() throws Exception {
		
		// 새로운 로그파일명을 만든다.
		// 로그파일 설정값에서
		// 확장자와 확장자 앞부분을 추출
		String logPath = RootConfig.LOG_FILE_PATH.getValue();
		int pointPos = logPath.lastIndexOf(".");
		int separatorPos = logPath.lastIndexOf(File.separator);
		
		if(pointPos < 0 || pointPos <= separatorPos) {
			// 마지막포인트(.) 위치가 마지막 디렉토리 구분자 위치 보다 앞쪽이면
			// 확장자는 없는 것임 -> 마지막포인트(.)의 위치를 가장 마지막으로 옮김
			// ex) C:\test.out\log
			pointPos = logPath.length();
		}
		
		// TODO Locale 설정값으로 설정하도록 변경 해야함
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.HHmmssSSS", Locale.KOREA);
		File newLogFile = null;
		
		for(int index = 0; index < 10; index ++) {
			
			// 새로운 로그 파일명 생성
			StringBuilder newLogPathBuilder = new StringBuilder("");
			newLogPathBuilder.append(logPath.substring(0, pointPos))
				.append(".").append(dateFormat.format(new Date()))
				.append(logPath.substring(pointPos));
			
			newLogFile = new File(newLogPathBuilder.toString());
			if(newLogFile.exists() == false) {
				// 파일이 존재하지 않으면 새로운 로그 파일 생성 후 
				// 루프를 빠져나간다.
				if(newLogFile.createNewFile() == true) {
					// 현재로그 파일로 설정한다.
					this.logFile = newLogFile;
					break;
				}
			}
			
			// 파일이 기존에 이미 있거나, 파일생성에 실패하면 
			// 다시 파일명을 만들어 파일생성을 시도한다.
			// 최대 10회
		}
	}

	/**
	 * 오래된 로그 파일을 삭제한다.
	 */
	private void removeOldLogFiles() {
		
		try {
			
			// 로그파일 설정값에서
			// 확장자와 확장자 앞부분을 추출
			String logPath = RootConfig.LOG_FILE_PATH.getValue();
			int pointPos = logPath.lastIndexOf(".");
			int separatorPos = logPath.lastIndexOf(File.separator);
			
			if(pointPos < 0 || pointPos <= separatorPos) {
				
				// 마지막포인트(.) 위치가 마지막 디렉토리 구분자 위치 보다 앞쪽이면
				// 확장자는 없는 것임 -> 마지막포인트(.)의 위치를 가장 마지막으로 옮김
				// ex) C:\test.out\log
				pointPos = logPath.length();
			}
			
			String logPrefix = logPath.substring(separatorPos + 1, pointPos);
			String logExt = logPath.substring(pointPos);
		
			Files
				// 로그파일 목록 추출
				.find(this.getLogFile().getParentFile().toPath(), 1
					, (path, attr) -> {
						String fileName = path.toFile().getName();
						
						if(this.getLogFile().getName().equals(fileName)) {
							// 현재 로그 파일은 제외
							return false;
						}
						
						return fileName.startsWith(logPrefix) && fileName.endsWith(logExt);
				})
				// 파일명 오름차순 정렬
				.sorted((path1, path2) -> {
					return path2.toString().compareTo(path1.toString());
				})
				// 상위 2개만 남기고 나머지 삭제 
				// TODO 몇개 남길건지 설정 변수화 해야함
				.skip(2)
				.forEach(path -> {
					try {
						Files.delete(path);
					} catch(Exception ex) {
						// TODO 
						ex.printStackTrace();
					}
				});
			
		} catch(Exception ex) {
			//TODO
			ex.printStackTrace();
		}
	}

	/**
	 * 로그 파일 검사 중단 여부
	 * @return 로그 파일 검사 중단 여부
	 */
	boolean isStop() {
		return this.isStop;
	}

	/**
	 * 로그 파일 검사 중단 여부 설정
	 * @param isStop 로그 파일 검사 중단 여부
	 */
	void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	/**
	 * 현재 로그 파일
	 * @return 현재 로그 파일
	 */
	File getLogFile() {
		return this.logFile;
	}
}
