package com.redeye.appagent.logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * 
 * @author jmsohn
 */
class Logger implements Runnable {
	
	/** 로그 메시지 입력 큐 */
	private BlockingQueue<String> inQ;
	
	/** 로그 Writer */
	private LogWriter writer;
	
	/** 중단 여부 */
	@Getter
	@Setter
	private boolean stop;
	
	/**
	 * 생성자
	 *
	 * @param inQ 로그 메시지 입력 큐
	 * @param writer 로그 Writer
	 */
	Logger(BlockingQueue<String> inQ, LogWriter writer) {
		
		this.inQ = inQ;
		this.writer = writer;
	}

	@Override
	public void run() {

		// 로그 메시지 변수
		String logMsg = null;

		// 중단 플래그가 없을 경우 무한 반복
		while(this.isStop() == false) {
			
			try {
				
				// 큐에서 로그 메시지 획득
				while(logMsg == null) {
					logMsg = this.inQ.poll(1000, TimeUnit.MILLISECONDS);
				}
				
				// 로그 메시지 write
				this.writer.write(logMsg);
				
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		} // End of while
	}
}
