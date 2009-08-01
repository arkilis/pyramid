package com.pyramidframework.ajax;

import javax.servlet.http.HttpServletRequest;

import uk.ltd.getahead.dwr.WebContextFactory;

import com.pyramidframework.upload.UploadProgress;

/**
 *  ��ȡ��ǰ���ļ��ϴ�����Ϣ
 * @author Mikab Peng
 *
 */
public class UploadProgressMonitor {
	
	
	/**
	 * ��ȡ��ǰ�ļ��ϴ�������Ϣ
	 * @return
	 */
	public UploadProgress getUploadInfo() {
		HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();
		

		if (req.getSession().getAttribute(UploadProgress.UPLOAD_ATTRIBUTE_KEY) == null)
			return new UploadProgress();
		return (UploadProgress) req.getSession().getAttribute(UploadProgress.UPLOAD_ATTRIBUTE_KEY);
	}
}
