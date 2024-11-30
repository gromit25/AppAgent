package com.redeye.babe.wrapper.io;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamWrapper extends OutputStream {
	
	protected OutputStream os;

	public OutputStreamWrapper(OutputStream os) {
		this.os = os;
	}

	@Override
	public void write(int b) throws IOException {
		this.os.write(b);
	}
	
	@Override
	public void write(byte b[]) throws IOException {
		this.os.write(b);
	}
	
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		this.os.write(b, off, len);
	}

	@Override
    public void flush() throws IOException {
		this.os.flush();
    }
	
	@Override
	public void close() throws IOException {
		this.os.close();
	}
}
