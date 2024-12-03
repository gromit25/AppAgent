package com.redeye.babe.agent.transform;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.redeye.babe.agent.exception.AgentException;
import com.redeye.babe.config.Config;
import com.redeye.babe.log.Log;

import lombok.Data;

/**
 * API 호출 변환 맵 Builder
 * 
 * @author jmsohn
 */
public class TransformMapConfigReader {
	
	/**
	 * API 호출 변환 설정 파일 읽음
	 * 
	 * @param transformConfigFile API 호출 변환 설정 파일
	 * @return API 호출 변환 설정 맵
	 */
	public static Map<String, TransformMap> readConfig(final File transformConfigFile) throws Exception {
		
		if(transformConfigFile == null || transformConfigFile.canRead() == false) {
			throw new AgentException("Config file is not readable : " + transformConfigFile.getAbsolutePath());
		}
		
		// TODO
		System.out.println("*** init BABE Agent : Read Transform Config File : " + transformConfigFile.getAbsolutePath());
		
		// API 호출 변환 맵 생성
		Map<String, TransformMap> transformMaps = new Hashtable<String, TransformMap>();
		
		// 파일에서 설정값들을 읽어옴
		List<String> reads = Files.readAllLines(transformConfigFile.toPath(), Config.SYSTEM_CHARSET.getValueObject(Charset.class));
		
		// 한줄씩 Parsing 하여 변환맵에 저장함
		// 예외 발생시 중단 시킴

		// 주석 라인 패턴
		Pattern commentP = Pattern.compile("^[ \\t]*#");
		// set 명령어 패턴
		Pattern setCmdP = Pattern.compile("^[ \\t]*set[ \\t]+(?<type>target|alter|joinpoint)[ \\t]+(?<params>.+)");
		// API 변환 패턴
		String transformPStr = "(?<staticF>static:)?(?<method>[^\\.\\(]+)(?<signature>\\([^\\)]*\\)[A-Z]([^\\;]*;)?)";
		Pattern transformP = Pattern.compile(transformPStr);
		
		BuilderConfig builderConfig = new BuilderConfig();
		
		for(String read : reads) {
			
			// 주석라인 여부 확인
			Matcher commentM = commentP.matcher(read);
			if(commentM.find() == true || read.trim().length() == 0) {
				continue;
			}
			
			// set 명령어 여부 확인
			Matcher setCmdM = setCmdP.matcher(read);
			if(setCmdM.matches() == true) {
				
				// set 명령일 경우, commmand type과 parameter 추출
				CmdType cmdType = CmdType.getCmdType(setCmdM.group("type"));
				String params = setCmdM.group("params");
				
				// command type 에 따라 설정값(builder config) 변경
				cmdType.setParams(builderConfig, params);
				
			} else {
				 
				// set 명령이 아닌 경우, 
				// API 변환 Parsing 수행
				List<TransformMap> parsedTransformMaps = TransformMapConfigReader.makeTransformMap(read, transformP, builderConfig);
				
				for(TransformMap transformMap : parsedTransformMaps) {
					
					transformMaps.put(transformMap.getTargetAPI(), transformMap);
					Log.writeAgentLog("%s -> %s", transformMap.getTargetAPI(), transformMap.getAltAPI());
				}
			}
		}
		
		return transformMaps;
	}
	
	/**
	 * 각 라인별 파싱 수행<br>
	 * (readConfig 메소드가 복잡한거 같아서 따로 뺌)
	 * 
	 * @param read
	 * @param transformP
	 * @param builderConfig
	 * @return
	 */
	private static List<TransformMap> makeTransformMap(final String read
			, final Pattern transformP, final BuilderConfig builderConfig) throws AgentException {
		
		// 명시적으로 변환 되는 것 이외에 묵시적으로
		// 추가되는 변환맵을 저장하기 위해 배열 형태를 사용
		List<TransformMap> transformMaps = new ArrayList<TransformMap>();
		
		// 1. 명시적 변환맵 파싱 및 추가
		// 변환 맵 생성
		TransformMap primaryTransformMap = new TransformMap();
		
		String[] configs = read.split("->", 2);
		
		// 2개 이상이면 오류 발생
		// A(target) -> B(alter)
		if(configs.length > 2) {
			throw new AgentException("Transform Configuration Error : " + read);
		}
		
		// target 메소드 명
		String targetMethod = configs[0].trim();

		// target 호출 parsing
		Matcher targetM = transformP.matcher(targetMethod);
		if(targetM.matches() == false) {
			throw new AgentException("Transform Configuration Error : " + read);
		}

		// target 메소드 설정함
		primaryTransformMap.setTargetAPIClass(builderConfig.getTargetClass());
		primaryTransformMap.setTargetAPIMethod(targetM.group("method"));
		primaryTransformMap.setTargetAPISignature(targetM.group("signature"));
		
		// static 메소드 여부
		boolean isStatic = (targetM.group("staticF") != null)?true:false;
		
		// 변환 메소드 명 설정
		String altMethod = "";
		if(configs.length == 2) {
			
			altMethod = configs[1].trim();
			
		} else {
			
			// 호출 API메소드가 생성자일 경우,
			// 변환 메소드 설정이 필수임
			// 변환 메소드가 없는 경우, 예외 발생
			if(primaryTransformMap.getTargetAPIMethod().equals("<init>")) {
				throw new AgentException("Transform Configuration Error : Constructor requires alternative method(" + read + ")");
			}
			
			// 변환메소드가 없을 경우 변환메소드를 API 호출 메소드명과 동일하게 만듦
			if(isStatic == true) {
				
				// static 메소드인 경우
				altMethod = primaryTransformMap.getTargetAPIMethod() + primaryTransformMap.getTargetAPISignature();
				
			} else {
				
				// static 메소드가 아닌경우
				// 앞에 "("을 뺀다.
				String tempSignature = primaryTransformMap.getTargetAPISignature().substring(1);
				// target class의 인스턴스를 받을 수 있도록
				// 앞쪽에 target 클래스를 삽입한다.
				altMethod = primaryTransformMap.getTargetAPIMethod() + "(L" + builderConfig.getTargetClass() + ";" + tempSignature;
				
			}
		}
		
		// 변환(alter) 호출 parsing 
		Matcher altM = transformP.matcher(altMethod);
		if(altM.matches() == false) {
			throw new AgentException("Transform Configuration Error : " + read);
		}
		
		// 변환(alter) 메소드 설정함
		primaryTransformMap.setAltAPIClass(builderConfig.getAltClass());
		primaryTransformMap.setAltAPIMethod(altM.group("method"));
		primaryTransformMap.setAltAPISignature(altM.group("signature"));
		
		// 변환맵 저장
		transformMaps.add(primaryTransformMap);
		
		// 2. 묵시적 추가 메소드
		if(primaryTransformMap.getTargetAPIMethod().equals("<init>")) {
			
			// 생성자일 경우, NEW 오퍼레이션의 생성 클래스를 변경 한다.
			TransformMap newTransformMap = new TransformMap();
			
			newTransformMap.setTargetAPIClass(primaryTransformMap.getTargetAPIClass());
			newTransformMap.setAltAPIClass(primaryTransformMap.getAltAPIClass());
			
			transformMaps.add(newTransformMap);
		}
		
		// 3. API 변환 수행 대상 판별 정보 설정
		for(TransformMap transformMap : transformMaps) {
			transformMap.setJoinPointExcludes(builderConfig.getJoinPointExcludes());
			transformMap.setJoinPointIncludes(builderConfig.getJoinPointIncludes());
		}
		
		return transformMaps;
	}
}

/**
 * 
 * @author jmsohn
 */
@Data
class BuilderConfig {
	
	/** 현재 target class */
	private String targetClass;
	
	/** 변환할 alternative class */
	private String altClass;
	
	/**
	 * 변환할 클래스인지 확인용 문자열 목록
	 * 목록에 포함되어 있으면, 현재 변환 맵을 이용하여 변환 수행
	 * @see com.redeye.babe.agent.transform.TransformMap
	 */
	private List<JoinPointInfo> joinPointIncludes;
	
	/**
	 * 변환할 클래스인지 확인용 문자열 목록
	 * 목록에 포함되어 있지 않으면, 현재 변환 맵을 이용하여 변환 수행
	 * @see com.redeye.babe.agent.transform.TransformMap
	 */
	private List<JoinPointInfo> joinPointExcludes;

	
	/**
	 * 생성자
	 */
	BuilderConfig() {
		this.clear();
	}
	
	/**
	 * 현재 설정 초기화
	 */
	void clear() {
		this.setTargetClass(null);
		this.setAltClass(null);
		this.setJoinPointIncludes(new Vector<>());
		this.setJoinPointExcludes(new Vector<>());
	}
}

/**
 * 
 * @author jmsohn
 */
enum CmdType {
	
	TARGET("target") {
		
		@Override
		void setParams(BuilderConfig builderConfig, final String params) throws Exception {
			
			if(builderConfig == null || params == null) return;
			
			builderConfig.clear();
			builderConfig.setTargetClass(params.trim());
		}
	},
	ALTER("alter") {
		
		@Override
		void setParams(BuilderConfig builderConfig, final String params) throws Exception {
			
			if(builderConfig == null || params == null) return;
			
			builderConfig.setAltClass(params.trim());
		}
	},
	JOINPOINT("joinpoint") {
		
		@Override
		void setParams(BuilderConfig builderConfig, final String params) throws Exception {
			
			if(builderConfig == null || params == null) return;
			
			Pattern joinPointP = Pattern.compile(
					"(?<className>[^\\.\\:]+)(\\.(?<methodName>[^\\:]+))?(\\:(?<lineNumber>[0-9]+))?");
			
			// 1. 
			String[] paramsAry = params.split("[ \\t]+");
			ArrayList<JoinPointInfo> joinPointInfos = new ArrayList<JoinPointInfo>();

			// paramsAry[0]은 include, exclude
			// paramsAry의 length는 1 보다 커야함
			if(paramsAry.length > 1) {
				
				for(int index = 1; index < paramsAry.length; index++) {
					
					Matcher joinPointM = joinPointP.matcher(paramsAry[index].trim());
					
					if(joinPointM.matches()) {
					
						String className = joinPointM.group("className");
						String methodName = joinPointM.group("methodName");
						String lineNumberStr = joinPointM.group("lineNumber");
						int lineNumber = -1; // 음수이면 미설정으로 간주한다.
						if(lineNumberStr != null) {
							lineNumber = Integer.parseInt(lineNumberStr);
						}
						
						joinPointInfos.add(new JoinPointInfo(className, methodName, lineNumber));
					}
				}
			}
			
			// 2.
			if(paramsAry[0].equals("include")) {
				builderConfig.setJoinPointIncludes(joinPointInfos);
			} else if(paramsAry[0].equals("exclude")) {
				builderConfig.setJoinPointExcludes(joinPointInfos);
			}
		}
	};
	
	//---------------------
	/**
	 * 
	 * @param builderConfig
	 * @param params
	 */
	abstract void setParams(BuilderConfig builderConfig, String params) throws Exception;
	
	//---------------------
	/** command string에 매칭되는 command 목록 */
	private static Hashtable<String, CmdType> cmds;
	
	/**
	 * Command Type 초기화
	 */
	static {
		
		cmds = new Hashtable<String, CmdType>();
		
		// 설정된 명령어(cmdStr) 별로  Command Type 등록
		for(CmdType cmd : CmdType.values()) {
			if(cmd.cmdStr != null) {
				cmds.put(cmd.cmdStr, cmd);
			}
		}
	}
	
	/**
	 * 주어진 명령어(cmdStr)에 등록된 Command Type을 리턴 
	 * @param cmdStr 명령어
	 * @return Command Type
	 */
	public static CmdType getCmdType(String cmdStr) {
		if(cmds != null) {
			return cmds.get(cmdStr);
		} else {
			return null;
		}
	}
	
	//---------------------
	/** command string */
	private String cmdStr;
	
	/**
	 * 생성자
	 * @param cmdStr commandstring
	 */
	private CmdType(String cmdStr) {
		this.cmdStr = cmdStr;
	}
}
