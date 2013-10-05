/**
 * 
 */
package com.apos.mobile;

import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

/**
 * @author Yuri Goron
 * 
 */
public class ServerThread implements Callable<Integer> {

	private static final Logger Log = Logger.getLogger(ServerThread.class);

	private boolean isStopped;

	public ServerThread() {
		Log.debug("Server Thread Initialized");
	}

	@Override
	public Integer call() throws Exception {
		Log.debug("Server Thread" + Thread.currentThread().getName()
				+ " Started");

		try {

			while (!isStopped) {
				Thread.sleep(10000);
//				Log.debug("Thread is alive");
			}

			Log.debug("Thread Stopped");
			return 0;

		} catch (InterruptedException ee) {
			Log.debug("Thread forced to finish");
			Thread.currentThread().interrupt();
		}

		Log.debug("Thread Canceled");
		return -1;
	}

	public synchronized boolean isStopped() {
		return isStopped;
	}

	public synchronized void setStopped(boolean isStopped) {
		this.isStopped = isStopped;
	}

}
