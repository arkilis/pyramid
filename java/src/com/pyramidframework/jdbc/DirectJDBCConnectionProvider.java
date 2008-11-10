package com.pyramidframework.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * 直接使用JDBC连接管理器获取连接
 * 
 * @author Mikab Peng
 * 
 */
public class DirectJDBCConnectionProvider implements ConnectionProvider {
	private String driverClassName = null;

	private String url = null;

	private String username = null;

	private String password = null;

	private Properties connectionProperties = null;

	public void closeConnection(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (Exception e) {
			// DO Nothing
		}
	}

	public Connection getConnection() {
		Properties props = new Properties(getConnectionProperties());
		if (username != null) {
			props.setProperty("user", username);
		}
		if (password != null) {
			props.setProperty("password", password);
		}

		try {
			return DriverManager.getConnection(getUrl(), props);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName.trim();
		try {
			Class.forName(this.driverClassName).newInstance();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setUrl(String url) {
		this.url = url.trim();
	}

	public String getUrl() {
		return url;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

	public Properties getConnectionProperties() {
		return connectionProperties;
	}

}
