package com.jeet.controller;

public final class JSpringApp {

	
	public static JAppContext run(ComponentScanner scanner){
		return new JAppContext(scanner.getAllComponents());
	}
}
