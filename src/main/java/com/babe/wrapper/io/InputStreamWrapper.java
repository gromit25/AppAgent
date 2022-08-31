package com.babe.wrapper.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamWrapper extends InputStream {
	
	protected InputStream is;
	
	public InputStreamWrapper(InputStream is) {
		this.is = is;
	}

	@Override
	public int read() throws IOException {
		int read = this.is.read();		
		return read;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		int readCnt = this.is.read(b);
		return readCnt;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int readCnt = this.is.read(b, off, len);
		return readCnt;
	}
	
	@Override
	public long skip(long n) throws IOException {
		return this.is.skip(n);
	}
	
	@Override
	public int available() throws IOException {
		return this.is.available();
	}
	
	@Override
	public void close() throws IOException {
		this.is.close();
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		this.is.mark(readlimit);
	}
	
	@Override
	public synchronized void reset() throws IOException {
		this.is.reset();
	}
	
	@Override
	public boolean markSupported() {
		return this.is.markSupported();
	}
}
