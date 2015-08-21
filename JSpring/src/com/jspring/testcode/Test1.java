package com.jspring.testcode;

import javax.sql.DataSource;

import com.jspring.annotations.Autowired;
import com.jspring.annotations.Component;
import com.jspring.annotations.Transational;

@Component(name="t1")
public class Test1 implements ITest {

	public void t1(){
		System.out.println("PRINT FROM:  Test1.t1()");
	}
	@Transational
	public void t2(){
		System.out.println("PRINT FROM:  Test1.t2()");
	}
    
	@Autowired
	public void setDS(DataSource ds){
		System.out.println("Test1.setDS()"+ds);
	}
	@Transational
	public String t3() {
		t2();
		//context.getBean("")
		System.out.println("PRINT FROM:  Test1.t3() HELLO datasource=  ");
		return "Hello";
	}
}
