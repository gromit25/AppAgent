package com.redeye.appagent.script.olexp.parser;

import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.instructions.LOAD_ELEMENT;
import com.redeye.appagent.script.instructions.NOP;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.parserfw.EndStatusType;
import com.redeye.appagent.script.parserfw.TransferBuilder;
import com.redeye.appagent.script.parserfw.TransferEventHandler;
import com.redeye.appagent.script.parserfw.TreeNode;

/**
 * 
 * 
 * @author jmsohn
 */
public class ElementParser extends AbstractParser<Instruction> {
	
	/** 변수명 혹은 메소드명 */
	private TreeNode<Instruction> indexNode;
	/** 배열 및 속성 노드 */
	private TreeNode<Instruction> tailNode;

	/**
	 * 생성자
	 */
	public ElementParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected void init() throws Exception {

		// 상태 변환 맵 추가
		this.putTransferMap("START", new TransferBuilder()
				.add("\\[", "INDEX")
				.add("^\\[", "ERROR")
				.build());
		
		this.putTransferMap("INDEX", new TransferBuilder()
				.add("\\]", "INDEX_END")
				.add("^\\]", "ERROR")
				.build());
		
		this.putTransferMap("INDEX_END", new TransferBuilder()
				.add("\\[", "NEW_ELEMENT", -1)
				.add("\\.", "NEW_ATTR")
				.add("^\\[\\.", "END", -1)
				.build());
		
		// 종료 상태 추가
		this.putEndStatus("INDEX_END");
		this.putEndStatus("NEW_ELEMENT", EndStatusType.IMMEDIATELY_END);
		this.putEndStatus("NEW_ATTR", EndStatusType.IMMEDIATELY_END);
		this.putEndStatus("END", EndStatusType.IMMEDIATELY_END); // ATTR_END 상태로 들어오면 Parsing을 중지
		this.putEndStatus("ERROR", EndStatusType.ERROR);
	}
	
	@TransferEventHandler(
			source={"START"},
			target={"INDEX"}
	)
	public void handleIndex(Event event) throws Exception {
		ArithmaticParser parser = new ArithmaticParser();
		this.indexNode = parser.parse(event.getReader());
	}
	
	@TransferEventHandler(
			source={"INDEX_END"},
			target={"NEW_ELEMENT"}
	)
	public void handleNewElement(Event event) throws Exception {
		ElementParser parser = new ElementParser();
		this.tailNode = parser.parse(event.getReader());
	}
	
	@TransferEventHandler(
			source={"INDEX_END"},
			target={"NEW_ATTR"}
	)
	public void handleNewAttr(Event event) throws Exception {
		AttrParser parser = new AttrParser();
		this.tailNode = parser.parse(event.getReader());
	}
	
	/**
	 * 파싱 종료 처리
	 */
	@Override
	protected void exit() throws Exception {
		
		//
		TreeNode<Instruction> rootNode = new TreeNode<>(new NOP());
		rootNode.addChild(this.indexNode);
		rootNode.addChild(new TreeNode<Instruction>(
			new LOAD_ELEMENT()
		));
		
		//
		if(this.tailNode != null) {
			rootNode.addChild(this.tailNode);
		}
		
		// Node로 설정
		this.setNode(rootNode);
	}
}
