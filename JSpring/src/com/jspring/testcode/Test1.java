package com.jspring.testcode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jspring.annotations.Cacheable;
import com.jspring.annotations.Component;
import com.jspring.annotations.Transational;
import com.jspring.controller.JAppContext;
import com.jspring.controller.JSpringApp;
import com.jspring.repository.intf.TransactionalRepositry;

@Component(name="t1")
public class Test1 implements ITest {
	@Cacheable
	public String t1(){
		System.out.println("PRINT FROM:  Test1.t1()");
		return "T!!!1";
	}
	@Transational
	public void t2(){
		System.out.println("PRINT FROM:  Test1.t2()");
		JAppContext context = JSpringApp.getAppContext();
		TransactionalRepositry tRep = (TransactionalRepositry)context.getBean("TransactionalRepositry");
		Connection con = tRep.getConnection();
		Connection con1 = tRep.getConnection();
		Connection con2 = tRep.getConnection();
		Connection con3= tRep.getConnection();
		try {
			System.out.println("Test1.t2()"+con.getClass());
			con.close();
			con1.close();
			con2.close();
			con3.close();
			con1.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
	@Transational
	public String t3() {
		t2();
		JAppContext context = JSpringApp.getAppContext();
		TransactionalRepositry tRep = (TransactionalRepositry)context.getBean("TransactionalRepositry");
		Connection con = tRep.getConnection();
		System.out.println("Test1.t3() con===="+con.getClass());
		try {
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery("select name from songs");
			while(rs.next()){
				System.out.println("Res: " +rs.getFetchSize()+"   rs=="+rs.getString(1));
			}
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("PRINT FROM:  Test1.t3() HELLO datasource");
		return "Hello";
	}
}
