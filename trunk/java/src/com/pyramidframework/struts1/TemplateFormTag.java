package com.pyramidframework.struts1;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.taglib.TagUtils;
import org.apache.struts.taglib.html.FormTag;

public class TemplateFormTag extends FormTag {
	private static final long serialVersionUID = -5014887840130839360L;

	public int doStartTag() throws JspException {
		if (this.action == null) {
			getAction();
		}
		return super.doStartTag();
	}
	
	/**
	 * 修正没有设置属性时，不释放资源
	 */
	public int doEndTag() throws JspException {		
		int result = super.doEndTag();
		release();
		return result;
	}

	/**
	 * 如果是Input结尾的Action，输出时转换为Submit结尾
	 */
	protected void renderAction(StringBuffer results) {
		String calcAction = getAction();
		calcAction = TagUtils.getInstance().getActionMappingURL(calcAction, this.pageContext);

		if (calcAction.endsWith("Input.do")) {
			calcAction = calcAction.substring(0, calcAction.length() - 8) + "Submit.do";
		}

		HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
		results.append(" action=\"");
		results.append(response.encodeURL(calcAction));
		results.append("\"");
	}

	public String getAction() {
		if (action == null) {
			HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			action = (String)request.getAttribute(Globals.ORIGINAL_URI_KEY);
		}
		return action;
	}

	protected void lookup() throws JspException {
		moduleConfig = TagUtils.getInstance().getModuleConfig(pageContext);
		mapping = (ActionMapping) moduleConfig.findActionConfig(this.getAction());
		beanName = mapping.getAttribute();
		beanScope = mapping.getScope();
	}
}
