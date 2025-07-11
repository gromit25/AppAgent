package com.redeye.appagent;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HWDiskStore;
import oshi.hardware.NetworkIF;

/**
 *
 * 
 * @author jmsohn
 */
class MetricsAcquisitor implements Runnable {
	
	/** */
	private SystemInfo sysInfo = new SystemInfo();
	
	/** */
	private MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
	
	/** */
	private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	
	
	@Override
	public void run() {
		
		//
		this.logJVMMetrics();
		
		//
		this.logSysMetrics();
	}

	/**
	 * 
	 */
	private void logJVMMetrics() {
		
		// JVM 메모리 사용
		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
		long max = heapMemoryUsage.getMax();
		long used = heapMemoryUsage.getUsed();
		
		System.out.println("memory max: " + max);
		System.out.println("memory used: " + used);
		
		// JVM의 스레드 개수
		int threadCnt = this.threadMXBean.getThreadCount();
		System.out.println("thread count: " + threadCnt);
	}
	
	/**
	 * 
	 */
	private void logSysMetrics() {
		
		// CPU
		CentralProcessor cpu = this.sysInfo.getHardware().getProcessor();

		System.out.println("Logical CPU count: " + cpu.getLogicalProcessorCount());
		System.out.println("System CPU load: " + cpu.getSystemCpuLoadTicks());
		
		// Memory
		GlobalMemory memory = this.sysInfo.getHardware().getMemory();

		System.out.println("Total memory: " + memory.getTotal());
		System.out.println("Available memory: " + memory.getAvailable());
		
		// Disk
		for (HWDiskStore disk : this.sysInfo.getHardware().getDiskStores()) {
			
			// 최신 정보로 갱신
		    disk.updateAttributes();
		    
		    System.out.println("Disk: " + disk.getName());
		    System.out.println("Read bytes: " + disk.getReadBytes());
		    System.out.println("Write bytes: " + disk.getWriteBytes());
		}
		
		// Network
		for (NetworkIF net : this.sysInfo.getHardware().getNetworkIFs()) {
			
			// 최신 상태 반영
		    net.updateAttributes();
		    
		    System.out.println("Interface: " + net.getName());
		    System.out.println("Bytes sent: " + net.getBytesSent());
		    System.out.println("Bytes received: " + net.getBytesRecv());
		}
	}
}
