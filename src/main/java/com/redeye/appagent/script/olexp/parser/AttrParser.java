package com.redeye.appagent.script.olexp.parser;

import com.redeye.appagent.script.instructions.LOAD_ATTR;
import com.redeye.appagent.script.instructions.NOP;
import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.parserfw.EndStatusType;
import com.redeye.appagent.script.parserfw.TransferBuilder;
import com.redeye.appagent.script.parserfw.TransferEventHandler;
import com.redeye.appagent.script.parserfw.TreeNode;

/**
 * 객체 속성명 파싱 클래스
 * 
 * @author jmsohn
 */
public class AttrParser extends AbstractParser<com.redeye.appagent.script.instructions.Instruction> {
	
	/** 변수명 혹은 메소드명 */
	private StringBuffer attrName;
	/** 배열 및 속성 노드 */
	private TreeNode<Instruction> tailNode;

	/**
	 * 생성자
	 */
	public AttrParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected void init() throws Exception {

		// 속성 초기화
		this.attrName = new StringBuffer();
		
		// 상태 변환 맵 추가
		this.putTransferMap("START", new TransferBuilder()
				.add("a-zA-Z\\_", "ATTR")
				.add("^a-zA-Z\\_", "ERROR")
				.build());
		
		this.putTransferMap("ATTR", new TransferBuilder()
				.add("a-zA-Z0-9\\_", "ATTR")
				.add("\\.", "NEW_ATTR")
				.add("\\[", "NEW_ELEMENT", -1)
				.add("^a-zA-Z0-9\\_\\.\\[", "ATTR_END", -1)
				.build());
		
		// 종료 상태 추가
		this.putEndStatus("ATTR");
		this.putEndStatus("NEW_ATTR", EndStatusType.IMMEDIATELY_END);
		this.putEndStatus("NEW_ELEMENT", EndStatusType.IMMEDIATELY_END);
		this.putEndStatus("ATTR_END", EndStatusType.IMMEDIATELY_END); // ATTR_END 상태로 들어오면 Parsing을 중지
		this.putEndStatus("ERROR", EndStatusType.ERROR);
	}
	
	@TransferEventHandler(
			source={"START", "ATTR"},
			target={"ATTR"}
	)
	public void handleAttr(Event event) throws Exception {
		this.attrName.append(event.getCh());
	}
	
	@TransferEventHandler(
			source={"ATTR"},
			target={"NEW_ATTR"}
	)
	public void handleNewAttr(Event event) throws Exception {
		AttrParser parser = new AttrParser();
		this.tailNode = parser.parse(event.getReader());
	}
	
	@TransferEventHandler(
			source={"ATTR"},
			target={"NEW_ELEMENT"}
	)
	public void handleElement(Event event) throws Exception {
		ElementParser parser = new ElementParser();
		this.tailNode = parser.parse(event.getReader());
	}
	
	/**
	 * 파싱 종료 처리
	 */
	@Override
	protected void exit() throws Exception {
		
		//
		TreeNode<Instruction> rootNode = new TreeNode<Instruction>(
			new LOAD_ATTR().addParam(this.attrName.toString())	// 속성명
		);
		
		//
		if(this.tailNode != null) {
			
			//
			TreeNode<Instruction> attrNode = rootNode;
			
			//
			rootNode = new TreeNode<>(new NOP());
			rootNode.addChild(attrNode);
			rootNode.addChild(this.tailNode);
		}
		
		// Node로 설정
		this.setNode(rootNode);
	}
}
