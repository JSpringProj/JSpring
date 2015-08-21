package com.jspring.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jspring.annotations.Transational;
import com.jspring.controller.JAppContext;
import com.jspring.controller.JSpringApp;
import com.jspring.repository.intf.TransactionalRepositry;
import com.jspring.util.AnnotationUtil;
import com.jspring.util.Logger;
import com.jspring.util.ReflectionUtil;

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
			JAppContext appCon = JSpringApp.getAppContext();
			TransactionalRepositry rep = (TransactionalRepositry)appCon.getBean("TransactionalRepositry");
			double d = rep.startTransaction();
			rep.getConnection();
			Logger.log(this, "invoke", " Started transaction with transaction id : "+d);
			retVal = method.invoke(proxyObj, args);
			rep.commit(d);
			Logger.log(this, "invoke", " Commited transaction with transaction id : "+d);
		}else{
			retVal = method.invoke(proxyObj, args);
		}

		return retVal;
	}
}
