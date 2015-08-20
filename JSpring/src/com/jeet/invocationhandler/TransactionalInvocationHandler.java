package com.jeet.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jeet.annotations.Transational;
import com.jeet.util.AnnotationUtil;
import com.jeet.util.ReflectionUtil;

public class TransactionalInvocationHandler implements InvocationHandler {
  private Object actualObj;
  private Object proxyObj;
	public TransactionalInvocationHandler(Object actualObj, Object proxyObj) {
		this.actualObj = actualObj;
		this.proxyObj = proxyObj;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retVal = null;
		Method meth = ReflectionUtil.getActualMethod(actualObj.getClass(), method);
		if( AnnotationUtil.containAnnotation(meth, Transational.class)){
			System.out.println(" Started Transaction");
			retVal = method.invoke(proxyObj, args);
			System.out.println("Transaction completed");		
		}else{
			retVal = method.invoke(proxyObj, args);
		}

		return retVal;
	}
}
