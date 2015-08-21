package com.jspring.invocationhandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

public class ConnectionInvocationHandler implements InvocationHandler{

	private Connection connection;
	public ConnectionInvocationHandler(Connection connection){
		System.out
				.println("ConnectionInvocationHandler.ConnectionInvocationHandler()");
		this.connection = connection;
		System.out
				.println("ConnectionInvocationHandler.ConnectionInvocationHandler()-----------000000000000000000-----------");
	}

	
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Object retVal = null;
		System.out.println("ConnectionInvocationHandler.invoke()"+method.getName()+connection);
		// if( !method.getName().equalsIgnoreCase("close")){
			 retVal = method.invoke(connection, args);
		// }
		return retVal;
	}
}
