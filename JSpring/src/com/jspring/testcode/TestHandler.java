package com.jspring.testcode;

import com.jspring.controller.JAppContext;
import com.jspring.controller.JSpringApp;

public class TestHandler {
	
	public static void main(String[] args) {
		JAppContext context = JSpringApp.run(TestConfig.class);
		
		ITest t = (ITest)context.getBean("t1");
		System.out.println("TestHandler.main()"+t);
		System.out.println(t.t1());
		System.out.println(t.t1());
		System.out.println(t.t1());
		t.t3();
	}

}
