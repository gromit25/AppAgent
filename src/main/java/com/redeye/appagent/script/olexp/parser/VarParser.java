package com.redeye.appagent.script.olexp.parser;

import java.util.ArrayList;
import java.util.List;

import com.redeye.appagent.script.instructions.INVOKE;
import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.instructions.LOAD_FALSE;
import com.redeye.appagent.script.instructions.LOAD_NULL;
import com.redeye.appagent.script.instructions.LOAD_TRUE;
import com.redeye.appagent.script.instructions.LOAD_VAR;
import com.redeye.appagent.script.instructions.NOP;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.parserfw.EndStatusType;
import com.redeye.appagent.script.parserfw.TransferBuilder;
import com.redeye.appagent.script.parserfw.TransferEventHandler;
import com.redeye.appagent.script.parserfw.TreeNode;

/**
 * 변수 및 메소드 파서 수행
 * 
 * @author jmsohn
 */
public class VarParser extends AbstractParser<Instruction> {
	
	/** 변수명 혹은 메소드명 */
	private StringBuffer varName;
	/** 메소드 여부 */
	private boolean isMethod;
	/** 메소드의 파라미터 목록 */
	private List<TreeNode<Instruction>> params;
	/** 컬렉션(List, Map) 또는 객체 속성 노드 */
	private TreeNode<Instruction> tailNode;

	/**
	 * 생성자
	 */
	public VarParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected void init() throws Exception {

		// 속성 초기화
		this.varName = new StringBuffer();
		this.isMethod = false;
		this.params = new ArrayList<TreeNode<Instruction>>();
		this.tailNode = null;
		
		// 상태 변환 맵 추가
		this.putTransferMap("START", new TransferBuilder()
				.add(" \t", "START")
				.add("a-zA-Z\\_", "VAR")
				.add("^ \ta-zA-Z\\_", "ERROR")
				.build());
		
		this.putTransferMap("VAR", new TransferBuilder()
				.add("a-zA-Z0-9\\_", "VAR")
				.add("\\.", "ATTR")
				.add("\\[", "ELEMENT", -1)
				.add("\\(", "PARAM_START")
				.add("^a-zA-Z0-9\\_\\.\\[\\(", "VAR_END", -1)
				.build());
		
		this.putTransferMap("PARAM_START", new TransferBuilder()
				.add(" \t", "PARAM_START")
				.add("\\)", "PARAM_END")
				.add("^ \t\\)", "PARAM", -1)
				.build());
		
		this.putTransferMap("PARAM", new TransferBuilder()
				.add(" \t", "PARAM")
				.add("\\,", "COMMA")
				.add("\\)", "PARAM_END")
				.add("^ \t\\,\\)", "ERROR")
				.build());
		
		this.putTransferMap("COMMA", new TransferBuilder()
				.add(" \t", "COMMA")
				.add("^ \t)", "PARAM", -1)
				.build());
		
		// 종료 상태 추가
		this.putEndStatus("VAR");
		this.putEndStatus("ATTR", EndStatusType.IMMEDIATELY_END);
		this.putEndStatus("ELEMENT", EndStatusType.IMMEDIATELY_END);
		this.putEndStatus("VAR_END", EndStatusType.IMMEDIATELY_END); // VAR_END 상태로 들어오면 Parsing을 중지
		this.putEndStatus("PARAM_END", EndStatusType.IMMEDIATELY_END); // PARAM_END 상태로 들어오면 Parsing을 중지
		this.putEndStatus("ERROR", EndStatusType.ERROR);
	}
	
	@TransferEventHandler(
			source={"START", "VAR"},
			target={"VAR"}
	)
	public void handleVar(Event event) throws Exception {
		this.varName.append(event.getCh());
	}
	
	@TransferEventHandler(
			source={"VAR"},
			target={"ATTR"}
	)
	public void handleAttr(Event event) throws Exception {
		
		this.checkVarName();
		
		AttrParser parser = new AttrParser();
		this.tailNode = parser.parse(event.getReader());
	}
	
	@TransferEventHandler(
			source={"VAR"},
			target={"ELEMENT"}
	)
	public void handleElement(Event event) throws Exception {
		
		this.checkVarName();
		
		ElementParser parser = new ElementParser();
		this.tailNode = parser.parse(event.getReader());
	}
	
	@TransferEventHandler(
			source={"VAR"},
			target={"PARAM_START"}
	)
	public void handleParamStart(Event event) throws Exception {
		
		this.checkVarName();
		
		this.isMethod = true;
	}
	
	@TransferEventHandler(
			source={"PARAM_START", "COMMA"},
			target={"PARAM"}
	)
	public void handleParam(Event event) throws Exception {
		BooleanParser parser = new BooleanParser();
		this.params.add(parser.parse(event.getReader()));
	}
	
	/**
	 * 파싱 종료 처리
	 */
	@Override
	protected void exit() throws Exception {
		
		// VAR 노드
		TreeNode<Instruction> varNode = null;
		
		// 변수 명 가져옴
		String varNameStr = this.varName.toString();
		
		// 메소드 여부에 따라 처리
		if(this.isMethod == false) {
			
			if(varNameStr.equals("true") == true) {
				// true 일 경우
				varNode = new TreeNode<Instruction>(
						new LOAD_TRUE()
					);
				
			} else if(varNameStr.equals("false") == true) {
				// false 일 경우
				varNode = new TreeNode<Instruction>(
						new LOAD_FALSE()
					);
				
			} else if(varNameStr.equals("null") == true) {
				// null 일 경우
				varNode = new TreeNode<Instruction>(
						new LOAD_NULL()
					);
				
			} else {
				// 변수 일 경우
				varNode = new TreeNode<Instruction>(
					new LOAD_VAR().addParam(varNameStr)
				);
			}
			
		} else {
			
			// INVOKE 명령어 생성
			TreeNode<Instruction> invokeNode = new TreeNode<Instruction>(
				new INVOKE()
					.addParam(varNameStr)	// 메소드명
					.addParam(Integer.toString(this.params.size()))	// 파라미터수
			);
			
			// 메소드 parameter 추가
			for(TreeNode<Instruction> param: this.params) {
				invokeNode.addChild(param);
			}
			
			// VAR 노드로 설정
			varNode = invokeNode;
		}
		
		// tail 노드가 없는 경우 var 노드만 추가
		// tail 노드가 있는 경우 추가 수행
		if(this.tailNode == null) {
			
			this.setNode(varNode);
			
		} else {
			
			TreeNode<Instruction> rootNode = new TreeNode<>(new NOP());
			rootNode.addChild(varNode);
			rootNode.addChild(this.tailNode);
			
			this.setNode(rootNode);
		}
	}
	
	/**
	 * 변수명(varName)이 예약어(true, false, null) 인지 확인
	 * 
	 * @throws 변수명이 예약어일 경우 예외 발생
	 */
	private void checkVarName() throws Exception {
		
		String varNameStr = this.varName.toString();
		
		if(varNameStr.equals("true") == true
			|| varNameStr.equals("false") == true
			|| varNameStr.equals("null") == true) {
			
			throw new Exception("invalid variable name:" + varName);
		}
	}
}
