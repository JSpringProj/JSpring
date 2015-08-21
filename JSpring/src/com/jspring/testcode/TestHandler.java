package com.jspring.testcode;

import com.jspring.controller.ComponentScanner;
import com.jspring.controller.JAppContext;
import com.jspring.controller.JSpringApp;

public class TestHandler {
	
	public static void main(String[] args) {
		JAppContext context = JSpringApp.run(new ComponentScanner("com.jspring"));
		
		ITest t = (ITest)context.getBean("t1");
		System.out.println("TestHandler.main()"+t);
		//t.t3();
		t.t3();
		//t.t3();
	}

}
