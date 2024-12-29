package com.redeye.appagent.script.olexp.parser;

import com.redeye.appagent.script.instructions.EQUAL;
import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.instructions.NOT_EQUAL;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.parserfw.EndStatusType;
import com.redeye.appagent.script.parserfw.TransferBuilder;
import com.redeye.appagent.script.parserfw.TransferEventHandler;
import com.redeye.appagent.script.parserfw.TreeNode;

/**
 * 동일 여부(==, !=) 연산 파싱 수행
 * 
 * @author jmsohn
 */
public class EqualityParser extends AbstractParser<Instruction> {
	
	/** 동일 여부 연산의 첫번째 파라미터의 tree node */
	private TreeNode<Instruction> p1;
	/** 동일 여부 연산의 두번째 파라미터의 tree node */
	private TreeNode<Instruction> p2;
	/** 동일 여부 연산 */
	private Instruction op;
	/** 동일 여부 연산 버퍼 */
	private StringBuffer opBuffer;

	/**
	 * 생성자
	 */
	public EqualityParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected void init() throws Exception {
		
		// 속성 변수 초기화
		this.p1 = null;
		this.p2 = null;
		this.op = null;
		
		this.opBuffer = new StringBuffer("");
		
		// 상태 전이 맵 설정
		this.putTransferMap("START", new TransferBuilder()
				.add(" \t", "START")
				.add("^ \t", "COMPARISON_1", -1)
				.build());
		
		this.putTransferMap("COMPARISON_1", new TransferBuilder()
				.add(" \t", "COMPARISON_1")
				.add("\\=\\!", "OPERATION")
				.add("^ \t\\=\\!", "END", -1)
				.build());
		
		this.putTransferMap("OPERATION", new TransferBuilder()
				.add("\\=", "COMPARISON_2")
				.add("^\\=", "ERROR")
				.build());
		
		this.putTransferMap("COMPARISON_2", new TransferBuilder()
				.add(" \t", "COMPARISON_2")
				.add("\\=\\!", "OPERATION")
				.add("^ \t\\=\\!", "END", -1)
				.build());
		
		// 종료 상태 추가
		this.putEndStatus("COMPARISON_1");
		this.putEndStatus("COMPARISON_2");
		this.putEndStatus("END", EndStatusType.IMMEDIATELY_END); // END 상태로 들어오면 Parsing을 중지
		this.putEndStatus("ERROR", EndStatusType.ERROR);
		
	}
	
	/**
	 * 첫번째 파라미터 상태로 전이시 핸들러 메소드
	 * 
	 * @param event 상태 전이 이벤트 정보
	 */
	@TransferEventHandler(
			source={"START"},
			target={"COMPARISON_1"}
	)
	public void handleP1(Event event) throws Exception {
		
		ComparisonParser parser = new ComparisonParser();
		this.p1 = parser.parse(event.getReader());

	}
	
	/**
	 * 동일 여부 연산자 상태로 전이시 핸들러 메소드
	 * 
	 * @param event 상태 전이 이벤트 정보
	 */
	@TransferEventHandler(
			source={"COMPARISON_1", "COMPARISON_2"},
			target={"OPERATION"}
	)
	public void handleOp1(Event event) throws Exception {
		this.opBuffer.append(event.getCh());
	}
	
	/**
	 * 두번째 파라미터 상태로 전이시 핸들러 메소드
	 * 
	 * @param event 상태 전이 이벤트 정보
	 */
	@TransferEventHandler(
			source={"OPERATION"},
			target={"COMPARISON_2"}
	)
	public void handleOp2(Event event) throws Exception {
		
		//
		if(this.op != null && this.p2 != null) {
			
			TreeNode<Instruction> newP1 = new TreeNode<Instruction>(this.op);
			newP1.addChild(this.p1);
			newP1.addChild(this.p2);
			
			this.p1 = newP1;
		}
		
		//
		this.opBuffer.append(event.getCh());
		
		switch(this.opBuffer.toString()) {
		case "==":
			this.op = new EQUAL();
			break;
		case "!=":
			this.op = new NOT_EQUAL();
			break;
		default:
			throw new Exception("Unexpected operation:" + op);
		}
		
		//
		this.opBuffer.delete(0, this.opBuffer.length());
		
		//
		ComparisonParser parser = new ComparisonParser();
		this.p2 = parser.parse(event.getReader());
		
	}
	
	/**
	 * 파싱 종료 처리
	 */
	@Override
	protected void exit() throws Exception {
		
		if(this.op != null && this.p2 != null) {
		
			// 동일 여부 연산이 존재하는 경우
			this.setNodeData(this.op);
			this.addChild(this.p1);
			this.addChild(this.p2);
			
		} else {
			
			// 동일 여부 연산이 존재하지 않는 경우
			this.setNode(this.p1);
		}
	}
}
