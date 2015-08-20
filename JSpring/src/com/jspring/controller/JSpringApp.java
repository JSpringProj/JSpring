package com.jspring.controller;

public final class JSpringApp {

	private static JAppContext appContext= null;
	
	public static JAppContext run(ComponentScanner scanner){
		appContext = new JAppContext(scanner.getAllComponents());
		return appContext;
	}
	
	public static JAppContext getAppContext(){
		return appContext;
	}
	
	
}
