package com.redeye.appagent.script.olexp.parser;

import com.redeye.appagent.script.instructions.ADD;
import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.instructions.MINUS;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.parserfw.EndStatusType;
import com.redeye.appagent.script.parserfw.TransferBuilder;
import com.redeye.appagent.script.parserfw.TransferEventHandler;
import com.redeye.appagent.script.parserfw.TreeNode;

/**
 * +,- 연산 파싱 수행
 * 
 * @author jmsohn
 */
public class ArithmaticParser extends AbstractParser<Instruction> {
	
	/** +,- 연산의 첫번째 파라미터의 tree node */
	private TreeNode<Instruction> p1;
	/** +,- 연산의 두번째 파라미터의 tree node */
	private TreeNode<Instruction> p2;
	/** +,- 연산 */
	private Instruction op;

	/**
	 * 생성자
	 */
	public ArithmaticParser() throws Exception {
		super();
	}

	/**
	 * 시작상태 반환
	 */
	@Override
	protected String getStartStatus() {
		return "START";
	}

	/**
	 * 초기화 수행
	 */
	@Override
	protected void init() throws Exception {
		
		// 속성 변수 초기화
		this.p1 = null;
		this.p2 = null;
		this.op = null;
		
		// 상태 전이 맵 설정
		this.putTransferMap("START", new TransferBuilder()
				.add(" \t", "START")
				.add("^ \t", "TERM", -1)
				.build());
		
		this.putTransferMap("TERM", new TransferBuilder()
				.add(" \t", "TERM")
				.add("\\+\\-", "OPERATION")
				.add("^ \t\\+\\-", "END", -1)
				.build());
		
		this.putTransferMap("OPERATION", new TransferBuilder()
				.add(" \t", "OPERATION")
				.add("^ \t", "ARITHMATIC", -1)
				.build());
		
		// 종료 상태 추가
		this.putEndStatus("TERM");
		this.putEndStatus("END", EndStatusType.IMMEDIATELY_END);
		this.putEndStatus("ARITHMATIC", EndStatusType.IMMEDIATELY_END);
	}

	/**
	 * +,-의 첫번째 파라미터 상태로 전이시 핸들러 메소드
	 * 
	 * @param event 상태 전이 이벤트 정보
	 */
	@TransferEventHandler(
			source={"START"},
			target={"TERM"}
	)
	public void handleP1(Event event) throws Exception {
		
		TermParser parser = new TermParser();
		this.p1 = parser.parse(event.getReader());

	}
	
	/**
	 * +,-의 연산자 상태로 전이시 핸들러 메소드
	 * 
	 * @param event 상태 전이 이벤트 정보
	 */
	@TransferEventHandler(
			source={"TERM"},
			target={"OPERATION"}
	)
	public void handleOp(Event event) throws Exception {
		
		if(event.getCh() == '+') {
			this.op = new ADD();
		} else if(event.getCh() == '-') {
			this.op = new MINUS();
		} else {
			throw new Exception("Unexpected operation:" + event.getCh());
		}
		
	}
	
	/**
	 * +,-의 첫번째 파라미터 상태로 전이시 핸들러 메소드
	 * 
	 * @param event 상태 전이 이벤트 정보
	 */
	@TransferEventHandler(
			source={"OPERATION"},
			target={"ARITHMATIC"}
	)
	public void handleP2(Event event) throws Exception {
		//
		ArithmaticParser parser = new ArithmaticParser();
		this.p2 = parser.parse(event.getReader());
	}
	
	/**
	 * 파싱 종료 처리
	 */
	@Override
	protected void exit() throws Exception {
		
		if(this.op != null && this.p2 != null) {
		
			// +,- 연산이 존재하는 경우
			this.setNodeData(this.op);
			this.addChild(this.p1);
			this.addChild(this.p2);
			
		} else {
			
			// +,- 연산이 존재하지 않는 경우
			this.setNode(this.p1);
		}
	}
}
