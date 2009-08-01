package com.pyramidframework.ajax;

import java.util.HashMap;
import java.util.Map;

import uk.ltd.getahead.dwr.Creator;
import uk.ltd.getahead.dwr.Messages;
import uk.ltd.getahead.dwr.impl.DefaultCreatorManager;


/**
 * 重载父类以实现解析时不判断该对象的类型
 * @author Mikab Peng
 *
 */
public class LazyCreatorManager extends DefaultCreatorManager {

	private Map creators = new HashMap();

	public LazyCreatorManager() {
		super.setCreators(creators);
	}

	public void addCreator(String scriptName, Creator creator) throws IllegalArgumentException {
		 Creator other = (Creator) creators.get(scriptName);
		if (other != null) {
			throw new IllegalArgumentException(Messages.getString("DefaultCreatorManager.DuplicateName", scriptName, other.getType().getName(), creator)); //$NON-NLS-1$
		}
		creators.put(scriptName, creator);
	}
}
