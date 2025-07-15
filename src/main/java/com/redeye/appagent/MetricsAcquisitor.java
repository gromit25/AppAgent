package com.redeye.appagent;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

import com.redeye.appagent.logger.Log;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;

/**
 * JVM 및 시스템 정보 수집기
 * 
 * @author jmsohn
 */
class MetricsAcquisitor implements Runnable {
	
	/** 시스템 정보 객체(OSHI) */
	private SystemInfo sysInfo = new SystemInfo();
	
	/** JVM 메모리 정보 객체 */
	private MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	
	/** JVM 스레드 정보 객체 */
	private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	
	
	@Override
	public void run() {
		
		// JVM 성능 정보 수집 로깅
		this.logJVMMetrics();
		
		// 시스템 정보 수집 및 로깅
		this.logSysMetrics();
	}

	/**
	 * JVM 정보 수집 및 로깅
	 */
	private void logJVMMetrics() {
		
		// JVM 메모리 사용
		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
		long max = heapMemoryUsage.getMax();
		long used = heapMemoryUsage.getUsed();
		
		Log.write("JVM_MTRC", "MEM", "\"max\": %d, \"used\": %d", max, used);
		
		// JVM의 스레드 개수
		int threadCnt = this.threadMXBean.getThreadCount();
		
		Log.write("JVM_MTRC", "THRD", "\"count\": %d", threadCnt);
	}
	
	/**
	 * 시스템 정보 수집 및 로깅
	 */
	private void logSysMetrics() {

		// ---- CPU
		CentralProcessor cpu = this.sysInfo.getHardware().getProcessor();
		
		long[][] prevTicks = cpu.getProcessorCpuLoadTicks();
		try {
			// 대기 시간 (예: 1초)
			Thread.sleep(1000);
		} catch(Exception ex) {
			// Do nothing
		}

		// CPU별 사용률 계산
		double[] cpuLoads = cpu.getProcessorCpuLoadBetweenTicks(prevTicks);

		// 전체 평균 사용률 계산
		double totalLoad = 0;
		for (double load : cpuLoads) {
			totalLoad += load;
		}

		double avgLoad = totalLoad / cpuLoads.length;
		Log.write("SYS_MTRC", "CPU",
				"\"usage\": %.2f",
				avgLoad * 100);
		
		// ---- Memory
		GlobalMemory mem = this.sysInfo.getHardware().getMemory();
		Log.write("SYS_MTRC", "MEM",
				"\"total\": %d, \"available\": %d",
				mem.getTotal(), mem.getAvailable());
	}
}
