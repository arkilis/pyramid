package com.pyramidframework.upload;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.fileupload.disk.DiskFileItem;

public class MonitoredDiskFileItem extends DiskFileItem {

	private static final long serialVersionUID = 1L;

	private MonitoredOutputStream out = null;

	private OutputStreamListener listener;

	public MonitoredDiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository, OutputStreamListener listener) {
		super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
		this.listener = listener;
	}

	public OutputStream getOutputStream() throws IOException {
		if (out == null) {
			out = new MonitoredOutputStream(super.getOutputStream(), listener);
		}
		return out;
	}
}
