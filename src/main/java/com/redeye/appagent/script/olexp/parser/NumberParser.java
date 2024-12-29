package com.redeye.appagent.script.olexp.parser;

import com.redeye.appagent.script.instructions.Instruction;
import com.redeye.appagent.script.instructions.LOAD_NUMBER;
import com.redeye.appagent.script.parserfw.AbstractParser;
import com.redeye.appagent.script.parserfw.EndStatusType;
import com.redeye.appagent.script.parserfw.TransferBuilder;
import com.redeye.appagent.script.parserfw.TransferEventHandler;

/**
 * 숫자 파서 수행
 * 
 * @author jmsohn
 */
public class NumberParser extends AbstractParser<Instruction> {
	
	/** 숫자 저장 버퍼 변수 */
	private StringBuffer buffer;

	/**
	 * 생성자
	 */
	public NumberParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	/**
	 * 파싱전 초기화 수행
	 */
	@Override
	protected void init() throws Exception {
		
		// 숫자 저장 버퍼 생성
		this.buffer = new StringBuffer();
		
		// 상태 변환 맵 추가
		this.putTransferMap("START", new TransferBuilder()
				.add("0-9", "NUMBER")
				.add("\\-", "SIGN")
				.add("^0-9\\-", "ERROR")
				.build());
		
		this.putTransferMap("SIGN", new TransferBuilder()
				.add("0-9", "NUMBER")
				.add("^0-9", "ERROR", -1)
				.build());
		
		this.putTransferMap("NUMBER", new TransferBuilder()
				.add("0-9", "NUMBER")
				.add("\\.", "DOT")
				.add("^0-9\\.", "END", -1)
				.build());
		
		this.putTransferMap("DOT", new TransferBuilder()
				.add("0-9", "FLOATING_NUMBER")
				.add("^0-9", "END", -1)
				.build());
		
		this.putTransferMap("FLOATING_NUMBER", new TransferBuilder()
				.add("0-9", "FLOATING_NUMBER")
				.add("^0-9", "END", -1)
				.build());
		
		// 종료 상태 추가
		this.putEndStatus("NUMBER");
		this.putEndStatus("FLOATING_NUMBER");
		this.putEndStatus("END", EndStatusType.IMMEDIATELY_END); // END 상태로 들어오면 Parsing을 중지
		this.putEndStatus("ERROR", EndStatusType.ERROR);
	}
	
	@TransferEventHandler(
			source={"START", "SIGN", "NUMBER", "DOT", "FLOATING_NUMBER"},
			target={"SIGN", "NUMBER", "DOT", "FLOATING_NUMBER"}
	)
	public void handleNumber(Event event) {
		this.buffer.append(event.getCh());
	}
	
	/**
	 * 파싱 종료 처리
	 */
	@Override
	protected void exit() throws Exception {
		
		// LOAD_NUMBER "숫자"
		LOAD_NUMBER inst = new LOAD_NUMBER();
		inst.addParam(this.buffer.toString());
		this.setNodeData(inst);
	}

}
