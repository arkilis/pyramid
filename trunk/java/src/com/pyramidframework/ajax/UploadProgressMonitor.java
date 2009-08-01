package com.pyramidframework.ajax;

import javax.servlet.http.HttpServletRequest;

import uk.ltd.getahead.dwr.WebContextFactory;

import com.pyramidframework.upload.UploadProgress;

/**
 *  获取当前的文件上传的信息
 * @author Mikab Peng
 *
 */
public class UploadProgressMonitor {
	
	
	/**
	 * 获取当前文件上传进度信息
	 * @return
	 */
	public UploadProgress getUploadInfo() {
		HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
		

		if (req.getSession().getAttribute(UploadProgress.UPLOAD_ATTRIBUTE_KEY) == null)
			return new UploadProgress();
		return (UploadProgress) req.getSession().getAttribute(UploadProgress.UPLOAD_ATTRIBUTE_KEY);
	}
}
