package com.redeye.appagent.script.parserfw;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;

import lombok.Getter;

/**
 * 파싱 수행시 문자열을 읽기 위한 Reader 클래스
 * -> 위치 정보를 획득하기 위한 용도
 * 
 * @author jmsohn
 */
public class ExpReader extends PushbackReader {
	
	/** 현재 읽고 있는 위치 */
	@Getter
	private int pos;

	/**
	 * 생성자
	 * 
	 * @param in 입력 스트림
	 */
	public ExpReader(Reader in) {
		super(in);
		this.pos = 0;
	}
	
	/**
	 * 생성자
	 * 
	 * @param in 입력 문자열
	 */
	public ExpReader(String in) {
		this(new StringReader(in));
	}
	
	/**
	 * 생성자
	 * 
	 * @param in 입력 스트림
	 * @param size 읽을 크기
	 */
	public ExpReader(Reader in, int size) {
		super(in, size);
		this.pos = 0;
	}
	
	@Override
	public int read() throws IOException {
		
		try {
			
			int read = super.read();
			if(read != -1) this.pos++;
			
			return read;
			
		} catch(IOException ioex) {
			
			this.pos--;
			throw ioex;
		}
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		
		this.pos += len;
		
		try {
			
			return super.read(cbuf, off, len);
			
		} catch(IOException ioex) {
			
			this.pos -= len;
			throw ioex;
		}
	}
	
	@Override
	public void unread(int c) throws IOException {
		
		this.pos--;
		
		try {
			
			super.unread(c);
			
		} catch(IOException ioex) {
			
			this.pos++;
			throw ioex;
		}
	}
	
	@Override
	public void unread(char[] cbuf, int off, int len) throws IOException {
		
		this.pos -= len;
		
		try {
			
			super.unread(cbuf, off, len);
			
		} catch(IOException ioex) {
			
			this.pos += len;
			throw ioex;
		}
	}
	
	@Override
	public void unread(char[] cbuf) throws IOException {
        this.unread(cbuf, 0, cbuf.length);
    }
	
	@Override
	public long skip(long n) throws IOException {
		
		this.pos += n;
		
		try {
			
			return super.skip(n);
			
		} catch(IOException ioex) {
			
			this.pos -= n;
			throw ioex;
		}
	}
}
