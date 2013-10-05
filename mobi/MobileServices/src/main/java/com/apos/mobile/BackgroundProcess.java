/**
 * 
 */
package com.apos.mobile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author Yuri Goron
 * 
 */
@Component
public class BackgroundProcess {
	private static final Logger Log = Logger.getLogger(BackgroundProcess.class);
	private ExecutorService executor;
	private ServerThread serverThread;

	public void startProcess() {
		Log.debug("BackGround Process Started");
		executor = Executors.newSingleThreadExecutor();
		serverThread= new ServerThread();
		executor.submit(serverThread); // Task should implement Runnable.
	}

	public void endProcess() {
		Log.debug("BackGround Process Finished");
		if (serverThread!=null) serverThread.setStopped(true);
		executor.shutdownNow();
	}
}
