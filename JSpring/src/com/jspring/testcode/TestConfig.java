package com.jspring.testcode;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.jspring.annotations.Bean;
import com.jspring.annotations.Component;
import com.jspring.annotations.Configuration;

@Component
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

}
