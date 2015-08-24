package com.jspring.testcode;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.jspring.annotations.Bean;
import com.jspring.annotations.Configuration;
import com.jspring.controller.JSpringApp;
import com.jspring.repository.impl.CacheRepositoryImpl;
import com.jspring.repository.impl.TransactionalRepositryImpl;
import com.jspring.repository.intf.CacheRepository;
import com.jspring.repository.intf.TransactionalRepositry;


//@ComponentScan(packageName="")
@Configuration
public class TestConfig {
	
	@Bean
	public DataSource getDataSource(){
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:mysql://localhost/springdb");

		// Define Username
		dataSource.setUsername("jatin");

		// Define Your Password
		dataSource.setPassword("6432");
		return dataSource;
	}
	
	@Bean
	public TransactionalRepositry getTransactionalRepositry(){
		TransactionalRepositry rep = new TransactionalRepositryImpl(getDataSource());
		return rep;
	}
	
	@Bean public CacheRepository getCacheRepository(){
		CacheRepository cacherep = new CacheRepositoryImpl();
		return cacherep;
	}

}
