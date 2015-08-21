package com.jspring.repository.impl;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.jspring.annotations.Autowired;
import com.jspring.annotations.Component;
import com.jspring.invocationhandler.ConnectionInvocationHandler;
import com.jspring.repository.intf.TransactionalRepositry;

@Component
public class TransactionalRepositryImpl implements TransactionalRepositry {

	private final ThreadLocal<TransactionHolder> threadLocal;
	
	@Autowired
	private DataSource dataSource;

	public TransactionalRepositryImpl() {
		threadLocal = new ThreadLocal<TransactionHolder>();
	}

	@Override
	public double startTransaction() {
		double transactionId = 0;
		TransactionHolder holder = getHolder();
		if ( Double.compare(holder.getMainTransactionId(), 0.0) == 0) {
			holder.init();
			transactionId = holder.getMainTransactionId();
			try {
				dataSource.getConnection().setAutoCommit(false);
			} catch (SQLException e) {
			}

		} else {
			transactionId = holder.getNextTransactionId();
		}
		return transactionId;
	}

	@Override
	public boolean commit(double transactionId) {
		TransactionHolder holder = getHolder();
		double mainTId = holder.getMainTransactionId();
		System.out.println("TransactionalRepositryImpl.commit() before"+transactionId+ "   "+mainTId +"   "+Double.compare(mainTId ,transactionId));
		if (Double.compare(mainTId ,transactionId) == 0) {
			try {
				Connection connection = (Connection) holder
						.getValue(TransactionHolder.ACTUAL_CONNECTION);
				System.out.println("TransactionalRepositryImpl.commit()"+transactionId+connection);
				connection.commit();
				System.out.println("TransactionalRepositryImpl.commit() 0-0-0-0-0-");
				connection.close();
				System.out.println("TransactionalRepositryImpl.commit() -------------");
				disposeHolder();
				
			} catch (SQLException e) {
			}
		} else {
			System.out.println("TransactionalRepositryImpl.commit() NOT EQ");
			return holder.removeTranstaionId(transactionId);
		}
		return true;
	}

	@Override
	public boolean rollback(double transactionId) {
		TransactionHolder holder = getHolder();
		double mainTId = holder.getMainTransactionId();
		if (Double.compare(mainTId ,transactionId) == 0) {
			try {
				Connection connection = (Connection) holder
						.getValue(TransactionHolder.ACTUAL_CONNECTION);
				connection.rollback();
				connection.close();
				disposeHolder();
				System.out.println("TransactionalRepositryImpl.rollback()"+transactionId);
			} catch (SQLException e) {
			}
		} else {
			return holder.removeTranstaionId(transactionId);
		}
		return true;
	}

	@Override
	public Connection getConnection() {
		TransactionHolder holder = getHolder();
		Connection con = (Connection) holder
				.getValue(TransactionHolder.PROXY_CONNECTION);
		if (con == null) {
			try {
				Connection actualConnection = dataSource.getConnection();
				holder.setValue(TransactionHolder.ACTUAL_CONNECTION,
						actualConnection);
				con = getConnectionProxy(actualConnection);
				holder.setValue(TransactionHolder.PROXY_CONNECTION, con);
			} catch (SQLException e) {
			}

		}
		System.out
				.println("********************8TransactionalRepositryImpl.getConnection()"
						+ con);
		return con;
	}

	private Connection getConnectionProxy(Connection actualConnection) {
		Connection con = null;
		con = (Connection) Proxy.newProxyInstance(actualConnection.getClass()
				.getClassLoader(), new Class[] { Connection.class },
				new ConnectionInvocationHandler(actualConnection));
		return con;
	}

	private TransactionHolder getHolder() {
		TransactionHolder holder = threadLocal.get();
		if (holder == null) {
			holder = new TransactionHolder();
			threadLocal.set(holder);
		}
		return holder;
	}

	private void disposeHolder() {
		TransactionHolder holder = getHolder();
		holder.dispose();
		holder = null;
		threadLocal.set(null);
	}

}
