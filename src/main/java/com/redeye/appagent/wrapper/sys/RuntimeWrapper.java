package com.redeye.appagent.wrapper.sys;

import java.io.IOException;

public class RuntimeWrapper {
	
	public static Process exec(Runtime rt, String command) throws IOException {
		
		System.out.println("execute command:\n" + command);
		return rt.exec(command);
	}

}
