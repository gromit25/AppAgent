package com.babe.wrapper.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import com.babe.agent.BabeAgent;
import com.babe.config.RootConfig;
import com.babe.log.Log;

import sizeof.agent.SizeOfAgent;

public class CollectionWrapper {
	
	//------------------
	
	public static <K, V> HashMap<K, V> initHashMap() {
		
		HashMap<K, V> map = new HashMap<K, V>();
		addWatchObj(map);
		
		return map;
	}
	
	public static <K, V> HashMap<K, V> initHashMap(int capacity) {
		
		HashMap<K, V> map = new HashMap<K, V>(capacity);
		addWatchObj(map);
		
		return map;
	}
	
	public static <K, V> HashMap<K, V> initHashMap(int capacity, float loadFactor) {
		
		HashMap<K, V> map = new HashMap<K, V>(capacity, loadFactor);
		addWatchObj(map);
		
		return map;
	}
	
	public static <K, V> HashMap<K, V> initHashMap(Map<K, V> initMap) {
		
		HashMap<K, V> map = new HashMap<K, V>(initMap);
		addWatchObj(map);
		
		return map;
	}
	
	//------------------
	
	public static <E> HashSet<E> initHashSet() {
		
		HashSet<E> set = new HashSet<E>();
		addWatchObj(set);
		
		return set;
	}
	
	public static <E> HashSet<E> initHashSet(int capacity) {
		
		HashSet<E> set = new HashSet<E>(capacity);
		addWatchObj(set);
		
		return set;
	}
	
	public static <E> HashSet<E> initHashSet(Collection<? extends E> c) {
		
		HashSet<E> set = new HashSet<E>(c);
		addWatchObj(set);
		
		return set;
	}
	
	//------------------
	
	public static <E> ArrayList<E> initArrayList() {
		
		ArrayList<E> list = new ArrayList<E>();
		addWatchObj(list);
		
		return list;
	}
	
	public static <E> ArrayList<E> initArrayList(int capacity) {
		
		ArrayList<E> list = new ArrayList<E>(capacity);
		addWatchObj(list);
		
		return list;
	}
	
	public static <E> ArrayList<E> initArrayList(Collection<? extends E> c) {
		
		ArrayList<E> list = new ArrayList<E>(c);
		addWatchObj(list);
		
		return list;
	}
	
	//------------------
	
	public static <E> Vector<E> initVector() {
		
		Vector<E> list = new Vector<E>();
		addWatchObj(list);
		
		return list;
	}
	
	public static <E> Vector<E> initVector(int initialCapacity) {
		
		Vector<E> list = new Vector<E>(initialCapacity);
		addWatchObj(list);
		
		return list;
	}
	
	public static <E> Vector<E> initVector(int initialCapacity, int capacityIncrement) {
		
		Vector<E> list = new Vector<E>(initialCapacity, capacityIncrement);
		addWatchObj(list);
		
		return list;
	}
	
	public static <E> Vector<E> initVector(Collection<? extends E> c) {
		
		Vector<E> list = new Vector<E>(c);
		addWatchObj(list);
		
		return list;
	}
	
	//------------------
	
	public static <E> LinkedList<E> initLinkedList() {
		
		LinkedList<E> list = new LinkedList<E>();
		addWatchObj(list);
		
		return list;
	}
	
	//------------------
	
	public static <K, V> LinkedHashMap<K, V> initLinkedHashMap() {
		
		LinkedHashMap<K, V> map = new LinkedHashMap<K, V>();
		addWatchObj(map);
		
		return map;
	}
	
	public static <K, V> LinkedHashMap<K, V> initLinkedHashMap(Map<? extends K, ? extends V> m) {
		
		LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(m);
		addWatchObj(map);
		
		return map;
	}
	
	public static <K, V> LinkedHashMap<K, V> initLinkedHashMap(int initialCapacity) {
		
		LinkedHashMap<K, V> map = new LinkedHashMap<K, V>(initialCapacity);
		addWatchObj(map);
		
		return map;
	}
	//------------------
	
	public static <E> LinkedBlockingQueue<E> initLinkedBlockingQueue() {
		
		LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<E>();
		addWatchObj(queue);
		
		return queue;
	}
	
	public static <E> LinkedBlockingQueue<E> initLinkedBlockingQueue(int initialCapacity) {
		
		LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<E>(initialCapacity);
		addWatchObj(queue);
		
		return queue;
	}
	
	public static <E> LinkedBlockingQueue<E> initLinkedBlockingQueue(Collection<? extends E> c) {
		
		LinkedBlockingQueue<E> queue = new LinkedBlockingQueue<E>(c);
		addWatchObj(queue);
		
		return queue;
	}
	
	//------------------
	
	public static <K, V> ConcurrentHashMap<K, V> initConcurrentHashMap() {
		
		ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>();
		addWatchObj(map);
		
		return map;
	}
	
	public static <K, V> ConcurrentHashMap<K, V> initConcurrentHashMap(int capacity, float loadFactor) {
		
		ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>(capacity, loadFactor);
		addWatchObj(map);
		
		return map;
	}
	
	public static <K, V> ConcurrentHashMap<K, V> initConcurrentHashMap(int capacity, float loadFactor, int concurrencyLevel) {
		
		ConcurrentHashMap<K, V> map = new ConcurrentHashMap<K, V>(capacity, loadFactor, concurrencyLevel);
		addWatchObj(map);
		
		return map;
	}
	
	//------------------
	
	public static <K extends Enum<K>, V> EnumMap<K, V> initEnumMap(Class<K> keyType) {
		
		EnumMap<K, V> map = new EnumMap<K, V>(keyType);
		addWatchObj(map);
		
		return map;
	}
	
	public static <K extends Enum<K>, V> EnumMap<K, V> initEnumMap(Map<K, ? extends V> m) {
		
		EnumMap<K, V> map = new EnumMap<K, V>(m);
		addWatchObj(map);
		
		return map;
	}
	
	//------------------
	
	private static ThreadLocal<Set<?>> targetSet = new ThreadLocal<>();
	
	public static Iterator<?> iterator(Set<?> set) {
		
		targetSet.set(set);
		return set.iterator();
	}
	
	public static boolean hasNext(Iterator<?> iter) {
		
		Set<?> set = targetSet.get();
		
		if(set != null) {
			Log.writeLog("COLLECTION", set, 0, "SIZE\t%d\t%d", set.size(), SizeOfAgent.fullSizeOf(set)/8);
		} else {
			Log.writeLog("COLLECTION", set, 0, "NULL");
		}
		
		return iter.hasNext();
	}
	
	//------------------
	private static void addWatchObj(Object obj) {
		
		try {
			
			StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
			StackTraceElement createStack = null;
			boolean isReadyToGetCreateStack = false;
			
			for(StackTraceElement stack : stacks) {
				
				if(true == stack.getClassName().startsWith(RootConfig.AGENT_PACKAGE.getValue())) {
					isReadyToGetCreateStack = true;
					continue;
				}
				
				if(true == isReadyToGetCreateStack) {
					createStack = stack;
					break;
				}
			}
			
			String createClassName = "";
			String createMethodName = "";
			int createLoc = 0;
			
			if(createStack != null) {
				createClassName = createStack.getClassName();
				createMethodName = createStack.getMethodName();
				createLoc = createStack.getLineNumber();
			}
			
			if(obj instanceof Collection) {
				BabeAgent.getCollectionWatcher().addCollectionObj((Collection<?>)obj, createClassName, createMethodName, createLoc);
			} else if(obj instanceof Map) {
				BabeAgent.getCollectionWatcher().addMapObj((Map<?,?>)obj, createClassName, createMethodName, createLoc);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
