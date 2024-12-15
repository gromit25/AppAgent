package com.redeye.appagent.wrapper.was;

import java.io.IOException;
import java.io.OutputStream;

import com.redeye.appagent.wrapper.io.OutputStreamWrapper;

public class ResponseOutputStreamWrapper extends OutputStreamWrapper {

	public ResponseOutputStreamWrapper(OutputStream out) {
		super(out);
	}
	
	@Override
	public void write(int b) throws IOException {
		super.write(b);
	}
	
	@Override
	public void write(byte b[]) throws IOException {
		super.write(b);
	}
	
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		super.write(b, off, len);
	}
}
