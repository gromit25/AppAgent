package com.redeye.appagent.util;

/**
 * byte array 처리 관련 utility 클래스
 * 
 * @author jmsohn
 */
public class ByteUtil {
	
	/**
	 * byte 배열을 문자열로 변환<br>
	 * ex) byte[] {26, 3} -> "1A03"<br>
	 * 
	 * @param bytes byte 배열
	 * @return 변환된 문자열
	 */
	public static String bytesToStr(byte[] bytes) throws Exception {
		
		if(bytes == null) {
			throw new NullPointerException("bytes is null.");
		}
		
		StringBuilder builder = new StringBuilder("");
		
		for(int index = 0; index < bytes.length; index++) {
			
			// 문자열에 바이트 추가
			builder.append(String.format("%02X", bytes[index]));
		}
		
		return builder.toString();
	}
}
