
package com.pyramidframework.upload;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UploadListener implements OutputStreamListener {
	private static final Log log = LogFactory.getLog(UploadListener.class);

	private HttpServletRequest request;

	private long delay = 0;

	private long startTime = 0;

	private long totalToRead = 0;

	private long totalBytesRead = 0;

	private int totalFiles = -1;

	public UploadListener(HttpServletRequest request, long debugDelay) {
		this.request = request;
		this.delay = debugDelay;
		totalToRead = request.getContentLength();
		this.startTime = System.currentTimeMillis();
	}

	public void bytesRead(long bytesRead) {
		totalBytesRead = totalBytesRead + bytesRead;
		updateUploadInfo("progress");

		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	public void done() {
		updateUploadInfo("done");
	}

	public void error(String message) {
		updateUploadInfo("error");
	}

	public void start() {
		totalFiles++;
		updateUploadInfo("start");
	}

	private void updateUploadInfo(String status) {
		long delta = (System.currentTimeMillis() - startTime) / 1000;
		
		request.getSession().setAttribute(UploadProgress.UPLOAD_ATTRIBUTE_KEY,
				new UploadProgress(totalFiles, totalToRead, totalBytesRead, delta, status));
	}

}
