package com.jspring.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

import com.jspring.util.Logger;

public class ConnectionInvocationHandler implements InvocationHandler{

	private Connection connection;
	public ConnectionInvocationHandler(Connection connection){
		this.connection = connection;
	}

	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retVal = null;
		 if( !method.getName().equalsIgnoreCase("close")){
			 retVal = method.invoke(connection, args);
		 }else{
			 Logger.log(this, "invoke", "Can not close connection on method call "+method.getName()+"().");
		 }
		return retVal;
	}
}
