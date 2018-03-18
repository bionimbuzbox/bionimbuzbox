package org.bionimbuzbox.dao;

import java.util.logging.Logger;

public class DAOFactory {
	
	private static final Logger LOGGER = Logger.getLogger(DAOFactory.class.getName());
	
	private DAODriver driver;
	
	private DAOFactory() {
	  
	}
	
	public static DAOFactory create(DAODriver driver) {
		LOGGER.info("Creating DAOFactory ...");
		DAOFactory factory = new DAOFactory();
		
		LOGGER.info("Configuring DAOFactory ...");
		factory.setDAODriver(driver);
		
		LOGGER.info("DAOFactory created!");
		return factory;
	}

  public void setDAODriver(DAODriver driver) {
    this.driver = driver;
  }

  public DAODriver getDAODriver() {
    return this.driver;
  }
	
	public static <T> T getDAOImplementation(DAODriver driver, Class<T> daoType) {
		T newInstance = null;
		try {
			String packageName = daoType.getPackage().getName() + "." + driver.getDriverName();
			String className = daoType.getSimpleName() + "Impl";
			newInstance = daoType.cast(
					Class.forName(packageName + "." + className)
					.getConstructor(DAODriver.class)
					.newInstance(driver)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newInstance;
	}
	
	 public WorkflowDAO getWorkflowDAO() {
	    return DAOFactory.getDAOImplementation(this.getDAODriver(), WorkflowDAO.class);
	  }
	  
	  public JobDAO getJobDAO() {
	    return DAOFactory.getDAOImplementation(this.getDAODriver(), JobDAO.class);
	  }
	  
	  public ResourceDAO getResourceDAO() {
	    return DAOFactory.getDAOImplementation(this.getDAODriver(), ResourceDAO.class);
	  }
	  
	  public ProviderDAO getProviderDAO() {
	    return DAOFactory.getDAOImplementation(this.getDAODriver(), ProviderDAO.class);
	  }
	  
	  public DatacenterDAO getDatacenterDAO() {
	    return DAOFactory.getDAOImplementation(this.getDAODriver(), DatacenterDAO.class);
	  }
	  
	  public ClusterDAO getClusterDAO() {
	    return DAOFactory.getDAOImplementation(this.getDAODriver(), ClusterDAO.class);
	  }
	  
	  public ServerDAO getServerDAO() {
	    return DAOFactory.getDAOImplementation(this.getDAODriver(), ServerDAO.class);
	  }
	  
	  public EventDAO getEventDAO() {
      return DAOFactory.getDAOImplementation(this.getDAODriver(), EventDAO.class);
    }
	  public EventHandlerDAO getEventHandlerDAO() {
      return DAOFactory.getDAOImplementation(this.getDAODriver(), EventHandlerDAO.class);
    }
}
