package org.xllapp.api.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 *
 * @author dylan.chen Aug 20, 2014
 * 
 */
public class ApplicationContextHolder implements ApplicationContextAware{

	private static ApplicationContext applicationContext;

	public static ApplicationContext getApplicationContext() {
		return ApplicationContextHolder.applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextHolder.applicationContext=applicationContext;		
	}
	
}
