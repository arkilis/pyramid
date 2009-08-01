
package com.pyramidframework.upload;

public class UploadProgress {
	
	public static final String UPLOAD_ATTRIBUTE_KEY = UploadProgress.class.getName() + ".Progress";
	
	private long totalSize = 0;

	private long bytesRead = 0;

	private long elapsedTime = 0;

	private String status = "done";

	private int fileIndex = 0;

	public UploadProgress() {
	}

	public UploadProgress(int fileIndex, long totalSize, long bytesRead, long elapsedTime, String status) {
		this.fileIndex = fileIndex;
		this.totalSize = totalSize;
		this.bytesRead = bytesRead;
		this.elapsedTime = elapsedTime;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public long getBytesRead() {
		return bytesRead;
	}

	public void setBytesRead(long bytesRead) {
		this.bytesRead = bytesRead;
	}

	public long getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public boolean isInProgress() {
		return "progress".equals(status) || "start".equals(status);
	}

	public int getFileIndex() {
		return fileIndex;
	}

	public void setFileIndex(int fileIndex) {
		this.fileIndex = fileIndex;
	}
}
