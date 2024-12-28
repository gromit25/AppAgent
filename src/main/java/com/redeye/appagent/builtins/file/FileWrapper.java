package com.redeye.appagent.builtins.file;

import java.io.File;
import java.net.URI;

public class FileWrapper extends File {

	/** 클래스 UID */
	private static final long serialVersionUID = 215886285249198664L;

	public FileWrapper(String pathName) {
		super(pathName);
	}
	
	public FileWrapper(URI uri) {
		super(uri);
	}
	
	public FileWrapper(File parent, String child) {
		super(parent, child);
	}
	
	public FileWrapper(String parent, String child) {
		super(parent, child);
	}

}
