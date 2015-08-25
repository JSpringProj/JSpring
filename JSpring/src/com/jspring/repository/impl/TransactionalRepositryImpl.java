package com.jspring.repository.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.jspring.repository.intf.TransactionalRepositry;
import com.jspring.util.Logger;

public class TransactionalRepositryImpl implements TransactionalRepositry {

	private final ThreadLocal<TransactionHolder> threadLocal;

	private DataSource dataSource;

	public TransactionalRepositryImpl(DataSource dataSource) {
		this.dataSource = dataSource;
		threadLocal = new ThreadLocal<TransactionHolder>();
	}

	@Override
	public double startTransaction() {
		double transactionId = 0;
		TransactionHolder holder = getHolder();
		if (Double.compare(holder.getMainTransactionId(), 0.0) == 0) {
			holder.init();
			transactionId = holder.getMainTransactionId();
			try {
				getConnection();
				Connection connection = (Connection) holder
						.getValue(TransactionHolder.ACTUAL_CONNECTION);
				connection.setAutoCommit(false);
			} catch (SQLException e) {
			}
		} else {
			transactionId = holder.getNextTransactionId();
		}
		Logger.log(this, "startTransaction", " Started Transaction with id : "
				+ transactionId);
		return transactionId;
	}

	@Override
	public boolean commit(double transactionId) {
		TransactionHolder holder = getHolder();
		double mainTId = holder.getMainTransactionId();
		if (Double.compare(mainTId, transactionId) == 0) {
			try {
				Connection connection = (Connection) holder
						.getValue(TransactionHolder.ACTUAL_CONNECTION);
				connection.commit();
				connection.close();
				disposeHolder();
				Logger.log(this, "commit", " Commited with id : "
						+ transactionId + ". Closing the connection");
			} catch (SQLException e) {
			}
		} else {
			return holder.removeTranstaionId(transactionId);
		}
		return true;
	}

	@Override
	public boolean rollback(double transactionId) {
		TransactionHolder holder = getHolder();
		double mainTId = holder.getMainTransactionId();
		if (Double.compare(mainTId, transactionId) == 0) {
			try {
				Connection connection = (Connection) holder
						.getValue(TransactionHolder.ACTUAL_CONNECTION);
				connection.rollback();
				connection.close();
				disposeHolder();
				Logger.log(this, "rollback", " Rollbacked with id : "
						+ transactionId + ". Closing the connection.");
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
				System.out
						.println("TransactionalRepositryImpl.getConnection() EXCEPTION------");
			}

		}
		return con;
	}

	private Connection getConnectionProxy(final Connection actualConnection) {
		Connection con = null;
		InvocationHandler handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				Object retVal = null;
				if (!method.getName().equalsIgnoreCase("close")) {
					retVal = method.invoke(actualConnection, args);
				} else {
					Logger.log(
							this,
							"invoke",
							"Can not close connection on method call "
									+ method.getName() + "().");
				}
				return retVal;
			}
		};
		con = (Connection) Proxy.newProxyInstance(actualConnection.getClass()
				.getClassLoader(), new Class[] { Connection.class }, handler);
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
