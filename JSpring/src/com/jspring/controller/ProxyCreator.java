package com.jspring.controller;

import java.lang.reflect.Proxy;

import com.jspring.annotations.Cachable;
import com.jspring.annotations.Loggable;
import com.jspring.annotations.Transational;
import com.jspring.invocationhandler.CachingIncocationHandler;
import com.jspring.invocationhandler.LoggingTransactionalHandler;
import com.jspring.invocationhandler.TransactionalInvocationHandler;
import com.jspring.util.AnnotationUtil;

public class ProxyCreator {

	public static Object getProxy(Class classs) {
		Object retVal = null;
		Object loggableProxy = getLoggableProxy(classs, null);
		Object cachableProxy = getCachablelProxy(classs, loggableProxy);
		Object transactionProxy = getTranstactionalProxy(classs, cachableProxy);
		retVal = transactionProxy != null ? transactionProxy : (cachableProxy != null ? cachableProxy : loggableProxy);
		return retVal;
	}

	private static Object getTranstactionalProxy(Class classs, Object proxyObj) {
		Object retVal = null;
		if (AnnotationUtil.containAllAnnotation(classs, Transational.class)) {
			Class[] interfaces = classs.getInterfaces();
			Object actualObj = getInstance(classs);
			retVal = Proxy.newProxyInstance(classs.getClassLoader(),
					interfaces, new TransactionalInvocationHandler(actualObj,
							(proxyObj != null ? proxyObj : actualObj)));
			System.out.println("Transactional proxy created is : "+retVal.getClass().getName());
		}
		return retVal;
	}

	private static Object getCachablelProxy(Class classs, Object proxyObj) {
		Object retVal = null;
		if (AnnotationUtil.containAllAnnotation(classs, Cachable.class)) {
			Class[] interfaces = classs.getInterfaces();
			Object actualObj = getInstance(classs);
			retVal = Proxy.newProxyInstance(classs.getClassLoader(),
					interfaces, new CachingIncocationHandler(actualObj, (proxyObj != null ? proxyObj : actualObj)));
			System.out.println("Cachable proxy created is : "+retVal.getClass().getName());
		}
		return retVal;
	}
	
	private static Object getLoggableProxy(Class classs, Object proxyObj) {
		Object retVal = null;
		if (AnnotationUtil.containAllAnnotation(classs, Loggable.class)) {
			Class[] interfaces = classs.getInterfaces();
			Object actualObj = getInstance(classs);
			retVal = Proxy.newProxyInstance(classs.getClassLoader(),
					interfaces, new LoggingTransactionalHandler(actualObj, (proxyObj != null ? proxyObj : actualObj)));
			System.out.println("Loggable proxy created is : "+retVal.getClass().getName());
		}
		return retVal;
	}
	

	private static Object getInstance(Class classs) {
		try {
			return Class.forName(classs.getName()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
