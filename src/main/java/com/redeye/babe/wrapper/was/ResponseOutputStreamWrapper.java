package com.redeye.babe.wrapper.was;

import java.io.IOException;
import java.io.OutputStream;

import com.redeye.babe.wrapper.io.OutputStreamWrapper;

public class ResponseOutputStreamWrapper extends OutputStreamWrapper {

	public ResponseOutputStreamWrapper(OutputStream out) {
		super(out);
	}
	
	@Override
	public void write(int b) throws IOException {
		System.out.println("DEBUG 000:");
		super.write(b);
	}
	
	@Override
	public void write(byte b[]) throws IOException {
		System.out.println("DEBUG 100:");
		super.write(b);
	}
	
	@Override
	public void write(byte b[], int off, int len) throws IOException {
		System.out.println("DEBUG 200:");
		super.write(b, off, len);
	}

}
