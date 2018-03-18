package org.bionimbuzbox.dao;

import java.util.Properties;

public interface DAODriver {

	public DAODriver configure(Properties properties);
	
	public DAODriver initialize();
	
	public void destroy();
	
	public String getDriverName();
}
