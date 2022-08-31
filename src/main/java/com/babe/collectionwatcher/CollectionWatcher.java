package com.babe.collectionwatcher;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.babe.log.Log;

import sizeof.agent.SizeOfAgent;

public class CollectionWatcher extends Thread {
	
	private ConcurrentHashMap<Integer, CollectionInfo<?>> collectionInfos;
	private boolean isStop;
	
	public CollectionWatcher(String args, Instrumentation inst) {
		// SizeOfAgent
		SizeOfAgent.premain(args, inst);
	}
	
	@Override
	public void run() {
		
		this.setStop(false);
		
		while(this.isStop() == false) {
			
			System.out.println("#######################");
			System.out.println("#### COUNT : " + this.getCollectionInfos().size());
			System.out.println("#######################");
			
			ArrayList<Integer> removedObjs = new ArrayList<Integer>();
			
			long collectionTime = System.currentTimeMillis();
			
			// 설정된 객체별로 각각 힙에서 차지하는 메모리 크기를 가져온다.
			this.getCollectionInfos().forEach((hash, objInfo) -> {
				if(objInfo.getCollectionObj() != null) {
					
					Log.writeLog("COLLECTION", objInfo.getCollectionObj(), 0
						, "%d\t%d\t%s\t%d\t%s.%s:%d"
						, objInfo.hashCode()
						, collectionTime
						, objInfo.getCollectionObj().getClass().getName()
						, objInfo.size()
						, objInfo.getCreateClassName()
						, objInfo.getCreateMethodName()
						, objInfo.getCreateLoc());

				} else {
					removedObjs.add(hash);
				}

			});
			
			// 삭제된 객체는 더이상 모니터링 하지 않는다.
			removedObjs.forEach(hash -> {
				this.getCollectionInfos().remove(hash);
			});
			
			try {
				Thread.sleep(5 * 1000);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void addCollectionObj(final Collection<?> collectionObj
			, final String createClassName, final String createMethodName
			, final int createLoc) throws Exception {
		
		if(collectionObj == null) {
			throw new Exception("collection object is null.");
		}
		
		CollectionTypeInfo collectionTypeInfo = new CollectionTypeInfo();
		collectionTypeInfo.setCollectionObj(collectionObj);
		collectionTypeInfo.setCreateClassName(createClassName);
		collectionTypeInfo.setCreateMethodName(createMethodName);
		collectionTypeInfo.setCreateLoc(createLoc);
		
		this.getCollectionInfos().put(collectionTypeInfo.hashCode(), collectionTypeInfo);
	}
	
	public void addMapObj(final Map<?, ?> mapObj
			, final String createClassName, final String createMethodName
			, final int createLoc) throws Exception {
		
		if(mapObj == null) {
			throw new Exception("collection object is null.");
		}
		
		MapTypeInfo mapTypeInfo = new MapTypeInfo();
		mapTypeInfo.setCollectionObj(mapObj);
		mapTypeInfo.setCreateClassName(createClassName);
		mapTypeInfo.setCreateMethodName(createMethodName);
		mapTypeInfo.setCreateLoc(createLoc);
		
		this.getCollectionInfos().put(mapTypeInfo.hashCode(), mapTypeInfo);
	}

	private ConcurrentHashMap<Integer, CollectionInfo<?>> getCollectionInfos() {
		
		if(this.collectionInfos == null) {
			this.collectionInfos = new ConcurrentHashMap<Integer, CollectionInfo<?>>();
		}
		
		return this.collectionInfos;
	}

	private boolean isStop() {
		return isStop;
	}

	private void setStop(boolean isStop) {
		this.isStop = isStop;
	}
}
