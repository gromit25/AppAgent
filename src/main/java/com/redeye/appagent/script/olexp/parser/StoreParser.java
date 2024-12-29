package com.redeye.appagent.script.olexp.parser;

import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.instructions.STORE;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.parserfw.TransferBuilder;
import com.redeye.appagent.script.parserfw.TransferEventHandler;

/**
 * L-value에 R-value를 저장 문장 파싱 수행
 * 
 * @author jmsohn
 */
public class StoreParser extends AbstractParser<Instruction> {
	
	/** L-value 변수명 저장 변수 */
	private StringBuffer lValueBuffer;

	/**
	 * 생성자
	 */
	public StoreParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected void init() throws Exception {
		
		// L-value 변수명 저장 버퍼 생성
		this.lValueBuffer = new StringBuffer("");
		
		// 상태 변환 맵 추가
		this.putTransferMap("START", new TransferBuilder()
				.add(" \t", "START")
				.add("a-zA-Z\\_", "L_VAR")
				.add("^ a-zA-Z\\_", "NOT_STORE_OP", Integer.MIN_VALUE)
				.build());
		
		this.putTransferMap("L_VAR", new TransferBuilder()
				.add("a-zA-Z0-9\\_", "L_VAR")
				.add(" \t", "L_VAR_BLANK")
				.add("=", "STORE_OP")
				.add("^ a-zA-Z0-9\\_=", "NOT_STORE_OP", Integer.MIN_VALUE)
				.build());
		
		this.putTransferMap("L_VAR_BLANK", new TransferBuilder()
				.add(" \t", "L_VAR_BLANK")
				.add("=", "STORE_OP")
				.add("^ \t=", "NOT_STORE_OP", Integer.MIN_VALUE)
				.build());
		
		this.putTransferMap("STORE_OP", new TransferBuilder()
				.add("^=", "STORE_OP_END", -1)
				.add("=", "NOT_STORE_OP", Integer.MIN_VALUE)
				.build());
		
		// 종료 상태 설정
		this.putEndStatus("L_VAR");
		this.putEndStatus("STORE_OP_END");
		this.putEndStatus("NOT_STORE_OP");
	}
	
	/**
	 * L-value 변수 파싱
	 * 
	 * @param event
	 */
	@TransferEventHandler(
			source={"START", "L_VAR"},
			target={"L_VAR"}
	)
	public void handleVar(Event event) throws Exception {
		this.lValueBuffer.append(event.getCh());
	}
	
	/**
	 * L-value 저장 파싱 완료시 수행
	 * 
	 * @param event
	 */
	@TransferEventHandler(
			source={"STORE_OP"},
			target={"STORE_OP_END"}
	)
	public void handleStoreOp(Event event) throws Exception {
		
		this.setNodeData(
			new STORE().addParam(this.lValueBuffer.toString())
		);
		
		this.addChild(
			new BooleanParser().parse(event.getReader())
		);
	}
	
	/**
	 * L-value 저장이 아닐 경우 파싱 수행
	 * 
	 * @param event
	 */
	@TransferEventHandler(
			source={"START", "L_VAR", "L_VAR_BLANK", "STORE_OP"},
			target={"NOT_STORE_OP"}
	)
	public void handleNotStoreOp(Event event) throws Exception {
		this.setNode(
			new BooleanParser().parse(event.getReader())
		);
	}
	
	@Override
	protected void exit() throws Exception {
		
		// L_VAR 상태에서 끝날 경우, 즉 변수만 있는 경우
		if(this.getStatus().equals("L_VAR") == true) {
			this.setNode(
				new BooleanParser().parse(this.lValueBuffer.toString())
			);
		}
	}
}
