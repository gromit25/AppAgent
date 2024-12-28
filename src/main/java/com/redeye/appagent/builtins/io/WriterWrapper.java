package com.redeye.appagent.builtins.io;

import java.io.IOException;
import java.io.Writer;

public class WriterWrapper extends Writer {
	
	private Writer out;
	
	public WriterWrapper(Writer out) {
		this.out = out;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		this.out.write(cbuf, off, len);
	}

	@Override
	public void flush() throws IOException {
		this.out.flush();
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}

}
