package ru.barabo.total.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StreamGobbler extends Thread {

	final static transient private Logger logger = Logger.getLogger(StreamGobbler.class.getName());

	private InputStream is;
	private String type;

	StreamGobbler(InputStream is, String type) {
		this.is = is;
		this.type = type;
	}

	public void run() {
		try {
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;

			while ((line = br.readLine()) != null) {
				if (type.equals("ERROR")) {
					logger.error(line);
				} else {
					logger.info(line);
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
			logger.error(ioe.getMessage());
		}
	}
}
