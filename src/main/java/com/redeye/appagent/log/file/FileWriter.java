package com.redeye.appagent.log.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.redeye.appagent.config.Config;
import com.redeye.appagent.log.LogWriter;

/**
 * 
 * 
 * @author jmsohn
 */
public class FileWriter implements LogWriter {
	
	private Path logFile;
	
	public FileWriter() {
		this.logFile = Paths.get(Config.LOG_FILE_PATH.getValue());
	}

	@Override
	public void write(String msg) throws Exception {
		Files.write(
			this.logFile,
			msg.getBytes(),
			StandardOpenOption.CREATE, StandardOpenOption.APPEND
		);
	}
}
