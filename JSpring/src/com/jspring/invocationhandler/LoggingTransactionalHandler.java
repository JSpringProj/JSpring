package com.jspring.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jspring.annotations.Loggable;
import com.jspring.util.AnnotationUtil;
import com.jspring.util.ReflectionUtil;

public class LoggingTransactionalHandler implements InvocationHandler {
	
	private Object invokedObj;
	private Object proxyObj;
	public LoggingTransactionalHandler(Object invokedObj, Object proxyObj) {
		this.invokedObj = invokedObj;
		this.proxyObj = proxyObj;
	}
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retVal = null;
		Method actualObj = ReflectionUtil.getActualMethod(invokedObj.getClass(), method);
		if( AnnotationUtil.containAnnotation(actualObj, Loggable.class)){
			Loggable loggable = (Loggable)actualObj.getAnnotation(Loggable.class);
			String methodName = actualObj.getName();
			System.out.println("Before method execution : "+methodName+" Message : "+ loggable.before());
			retVal = method.invoke(proxyObj, args);
			System.out.println("Method execution completed, method name is : "+methodName+" Message : "+ loggable.after());
		}else{
			retVal = method.invoke(proxyObj, args);
		}
		return retVal;
	}

}
