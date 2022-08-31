package com.babe.config;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

/**
 * agent 기본 설정 파일 변경 여부 모니터링 클래스
 * @author jmsohn
 */
public class RootConfigFileWatcher extends AbstractFileWatcher {
	
	/**
	 * 생성자
	 * @param configFile 기본 설정 파일
	 */
	public RootConfigFileWatcher(File configFile) throws Exception {
		super(configFile);
	}
	
	/**
	 * 생성자
	 * @param configFileName 기본 설정 파일 이름
	 */
	public RootConfigFileWatcher(String configFileName) throws Exception {
		super(configFileName);
	}
	
	@Override
	protected void read() throws Exception {
		
		// 1. 파일에서 속성들을 읽어옴
		Properties configProps = new Properties();
		configProps.load(new FileReader(this.getWatchedFile()));
		
		// 2. 각 속성에 읽어온 값을 설정함
		for(RootConfig configValue : RootConfig.values()) {
			
			String value = configProps.getProperty(configValue.getPropertyName());
			configValue.setValue(value);
		}
	}
}
