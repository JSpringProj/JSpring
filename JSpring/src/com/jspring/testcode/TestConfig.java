package com.jspring.testcode;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.jspring.annotations.Bean;
import com.jspring.annotations.Configuration;


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
	
	/*@Bean
	public TransactionalRepositry getTransactionalRepositry(){
		TransactionalRepositry rep = new TransactionalRepositryImpl();
		DataSource ds = (DataSource)JSpringApp.getAppContext().getBean("DataSource");
		rep.setDataSource(ds);
		return rep;
	}
	
	@Bean CacheRepository getCacheRepository(){
		CacheRepository cacherep = new CacheRepositoryImpl();
		return cacherep;
	}*/

}
