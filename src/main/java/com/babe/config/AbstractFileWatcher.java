package com.babe.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

/**
 * 설정 파일의 실시간 반영을 위한
 * 파일 변경 모니터링 클래스
 * @author jmsohn
 */
public abstract class AbstractFileWatcher extends Thread {
	
	//--------

	/** 파일 변경 여부를 모니터링하기 위한 watcher */
	private WatchService fileWatcher;
	/** 모니터링 파일 */
	private File watchedFile;
	/** 모니터링 파일 최종 변경 시간 */
	private long lastModifiedTime;
	/** 모니터링 중단 여부 */
	private boolean isStop;
	
	//---------
	
	/**
	 * 파일이 변경 되었을 때,
	 * 읽어 들이는 메소드
	 */
	protected abstract void read() throws Exception;

	/**
	 * 생성자
	 * @param watchedFile 모니터링 파일
	 */
	public AbstractFileWatcher(File watchedFile) throws Exception {
		
		this.setWatchedFile(watchedFile);
		
		// main 프로그램 종료시, 같이 종료되도록 Daemon 으로 설정한다.
		this.setDaemon(true);
		
		// 최초 생성시 한번 읽어 들임
		this.read();
	}
	
	/**
	 * 생성자
	 * @param watchedFileName 모니터링 파일 이름
	 */
	public AbstractFileWatcher(String watchedFileName) throws Exception {
		this(new File(watchedFileName));
	}
	
	@Override
	public void run() {
		
		try {
			
			//-----------------------
			// 파일 모니터링 시작
			this.setStop(false);
			this.fileWatcher = FileSystems.getDefault().newWatchService();
			
			// 파일 생성, 변경시 다시읽어 들인다.
			Path chasingListDirPath = this.getWatchedFile().getParentFile().toPath();
			chasingListDirPath.register(this.fileWatcher
					, StandardWatchEventKinds.ENTRY_CREATE
					, StandardWatchEventKinds.ENTRY_MODIFY);
			
			while(this.isStop() == false) {
				
				try {
				
					// 파일 변경 이벤트 catch
					WatchKey key = fileWatcher.take();
					
					for(WatchEvent<?> event : key.pollEvents()) {
						
						WatchEvent.Kind<?> kind = event.kind();
						Object context = event.context();
						
						// 이벤트가 발생한 파일이 현재 모니터링 중인 파일인지 확인한다.
						// 수정 시간이 이전에 읽어들인 시간과 동일하지 않으면 다시 읽어 들인다.
						if(context != null && context instanceof Path
								&& ((Path)context).toFile().getName().equals(this.getWatchedFile().getName())
								&& ((Path)context).toFile().lastModified() != this.lastModifiedTime) {
							
							this.lastModifiedTime = this.getWatchedFile().lastModified();
							
							// TODO
							// 삭제 예정
							System.out.println("Event is occured At ConfigWatcher:" + kind.toString() + ":" + this.getWatchedFile().getAbsolutePath());
							
							// 파일을 다시 읽어 들인다. 
							try {
								this.read();
							} catch(Exception ex) {
								// TODO
								// 읽다가 오류 날 경우, 
								ex.printStackTrace();
							}
							
							// TODO
							// 읽기 성공
							// 다시 이벤트를 기다린다.
							System.out.println("config is refreshed.");
						}
					}
				
					key.reset();
					
				} catch(ClosedWatchServiceException cwse) {
					
					System.out.println("Close Watch Event occured!");
					//---------------------------
					// ChasingListWatcher에서 발생한 이유로 Thread.sleep 넣음
					Thread.sleep(10);
					
				}
			}
			
		} catch (InterruptedException ie) {
			// TODO
			System.out.println("InterruptedException is occured!");
		} catch (IOException ioe) {
			// TODO
			System.out.println("IOException is occured!");
		} catch (Exception ex) {
			// TODO
			System.out.println("Exception is occured!");
		}
	}

	/**
	 * 모니터링 중단 여부 
	 * @return 모니터링 중단 여부
	 */
	public boolean isStop() {
		return this.isStop;
	}

	/**
	 * 모니터링 중단 여부 설정
	 * @param isStop 모니터링 중단 여부
	 */
	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	/**
	 * 모니터링 중인 파일
	 * @return 모니터링 중인 파일
	 */
	protected File getWatchedFile() throws Exception {
		this.isReadableFile(this.watchedFile);
		return this.watchedFile;
	}

	/**
	 * 모니터링 중인 파일 설정
	 * @param watchedFile 모니터링 중인 파일
	 */
	protected void setWatchedFile(File watchedFile) throws Exception {
		this.isReadableFile(watchedFile);
		this.lastModifiedTime = watchedFile.lastModified();
		this.watchedFile = watchedFile;
	}
	
	/**
	 * 파일이 읽을 수 있는 상태인지 검사
	 * @param watchedFile 모니터링 중인 파일
	 * @exception 파일이 읽을 수 없는 상태일 때, 예외 발생
	 */
	private void isReadableFile(File watchedFile) throws Exception {
		if(watchedFile == null) {
			throw new Exception("Watched file is null.");
		}
		
		if(watchedFile.exists() == false
			|| watchedFile.isFile() == false
			|| watchedFile.canRead() == false) {
			throw new Exception("Can't read file:" + watchedFile.getAbsolutePath());
		}
	}
}
