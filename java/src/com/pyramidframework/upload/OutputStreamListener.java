
package com.pyramidframework.upload;

public interface OutputStreamListener {
	void start();

	void bytesRead(long bytesRead);

	void error(String message);

	void done();
}
