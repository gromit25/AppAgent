package com.babe.agent.integrity;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.codec.digest.DigestUtils;

import com.babe.agent.exception.AgentException;
import com.babe.config.RootConfig;

public class IntegrityChecker {
	
	/**
	 * 라이브러리 파일 무결성 체크
	 * 무결성 체크된 파일의 class는 변환 작업 skip 처리함
	 * @param intigrityConfigFileName 무결성 체크 설정 정보 파일명 
	 * @return 무결성 체크 완료된 라이브러리 파일 목록
	 * @throws AgentException 무결성 체크 실패시 발생 예외
	 */
	public static HashSet<String> checkIntigrity(final File intigrityConfigFile) throws AgentException {
		
		//-----------------------------
		// 무결성 체크 설정 파일이 읽을 수 있는 상태인지 검사
		if(intigrityConfigFile == null || intigrityConfigFile.exists() == false
				|| intigrityConfigFile.canRead() == false) {
			throw new AgentException("intigrity check file is not readable : " + intigrityConfigFile.getAbsolutePath());
		}
		
		// TODO
		System.out.println("*** BABE : Read Intigrity Config File : " + intigrityConfigFile.getAbsolutePath());
		
		//------------------------------
		// 무결성 설정 정보 읽은 후 실제파일의 해시값과 비교함
		HashSet<String> checkOK = new HashSet<String>();
		
		try {
			
			Iterator<String> reads = Files.readAllLines(intigrityConfigFile.toPath()
					, RootConfig.SYSTEM_CHARSET.getValueObject(Charset.class)).iterator();
			
			while(reads.hasNext()) {
				
				//-------------------
				// 설정파일을 읽고 읽은 내용을 검증함
				String read = reads.next();
				if(read == null || read.trim().equals("")) {
					continue;
				}
				
				String[] config = read.split("\t");
				if(config.length != 2) {
					continue;
				}
				
				File libFile = new File(config[0]);
				if(!libFile.exists() || !libFile.isFile() || !libFile.canRead()) {
					continue;
				}
				
				//-------------------
				// 라이브러리 파일의 해시를 생성함
				byte[] libBytes = Files.readAllBytes(libFile.toPath());
				String libHash = DigestUtils.sha512Hex(libBytes);
				
				if(config[1].equals(libHash)) {
					// 해시값이 동일하면
					checkOK.add(libFile.getAbsolutePath());
				} else {
					// 해시값이 일치하지 않으면
					// TODO Agent에 경고함
					System.out.println("파일이 일치하지 않음");
				}
			}
			
		} catch (IOException ioe) {
			throw new AgentException(ioe.getMessage());
		}
		
		return checkOK;
	}

}
