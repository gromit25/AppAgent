package com.babe.wrapper.was;

import java.io.IOException;
import java.io.Writer;

import com.babe.wrapper.io.WriterWrapper;

public class ResponseWriterWrapper extends WriterWrapper {

	public ResponseWriterWrapper(Writer out) {
		super(out);
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		System.out.println("DEBUG WRITE ");
		super.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		System.out.println("DEBUG FLUSH ");
		super.flush();
	}

	@Override
	public void close() throws IOException {
		System.out.println("DEBUG CLOSE ");
		super.close();
	}
}
