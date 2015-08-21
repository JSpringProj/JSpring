package com.jspring.controller;

import java.lang.reflect.Proxy;

import com.jspring.annotations.CacheEvict;
import com.jspring.annotations.Cacheable;
import com.jspring.annotations.Loggable;
import com.jspring.annotations.Transational;
import com.jspring.invocationhandler.CachingInvocationHandler;
import com.jspring.invocationhandler.LoggingTransactionalHandler;
import com.jspring.invocationhandler.TransactionalInvocationHandler;
import com.jspring.util.AnnotationUtil;
import com.jspring.util.Logger;

public class ProxyCreator {

	public static Object getProxy(Object actualObj) {
		Object retVal = null;
		Object loggableProxy = getLoggableProxy(actualObj, actualObj);
		Object cachableProxy = getCachablelProxy(actualObj, loggableProxy);
		Object transactionProxy = getTranstactionalProxy(actualObj,
				cachableProxy);
		retVal = transactionProxy;
		return retVal;
	}

	private static Object getTranstactionalProxy(Object actualObj,
			Object proxyObj) {
		Object retVal;
		Class clz = actualObj.getClass();
		if (AnnotationUtil.containAllAnnotation(clz, Transational.class)) {
			Class[] interfaces = clz.getInterfaces();
			retVal = Proxy.newProxyInstance(clz.getClassLoader(), interfaces,
					new TransactionalInvocationHandler(actualObj,
							(proxyObj != null ? proxyObj : actualObj)));
			Logger.log("ProxyCreator", "getTranstactionalProxy",
					"Transactional proxy created is : "
							+ retVal.getClass().getName());
		} else {
			retVal = proxyObj;
		}
		return retVal;
	}

	private static Object getCachablelProxy(Object actualObj, Object proxyObj) {
		Object retVal;
		Class clz = actualObj.getClass();
		if (AnnotationUtil.containAllAnnotation(clz, new Class[] {
				Cacheable.class, CacheEvict.class })) {
			Class[] interfaces = clz.getInterfaces();
			retVal = Proxy.newProxyInstance(clz.getClassLoader(), interfaces,
					new CachingInvocationHandler(actualObj,
							(proxyObj != null ? proxyObj : actualObj)));
			Logger.log("ProxyCreator", "getCachablelProxy",
					"Cachable proxy created is : "
							+ retVal.getClass().getName());
		} else {
			retVal = proxyObj;
		}
		return retVal;
	}

	private static Object getLoggableProxy(Object actualObj, Object proxyObj) {
		Object retVal;
		Class clz = actualObj.getClass();
		if (AnnotationUtil.containAllAnnotation(clz, Loggable.class)) {
			Class[] interfaces = clz.getInterfaces();
			retVal = Proxy.newProxyInstance(clz.getClassLoader(), interfaces,
					new LoggingTransactionalHandler(actualObj,
							(proxyObj != null ? proxyObj : actualObj)));
			Logger.log("ProxyCreator", "getLoggableProxy",
					"Loggable proxy created is : "
							+ retVal.getClass().getName());
		} else {
			retVal = proxyObj;
		}
		return retVal;
	}
}
