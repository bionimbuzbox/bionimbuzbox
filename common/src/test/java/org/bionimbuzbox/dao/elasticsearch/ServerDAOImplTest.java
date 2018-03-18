package org.bionimbuzbox.dao.elasticsearch;

import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.ServerDAO;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Server;

public class ServerDAOImplTest {

  private DAODriver driver;
  private DAOFactory factory;
  private Logger LOGGER = Logger.getLogger(ServerDAOImplTest.class.getName());
  
  public ServerDAOImplTest() {
    
  }
  
  public void getAll() throws Exception {
    this.initialize();
    ServerDAO dao = factory.getServerDAO();
    
    List<Server> servers = dao.getAll(true);
    
    for (Server server : servers) {
      LOGGER.info(server.toString());
    }
    
    this.destroy();
    
  }
  
  public void get() throws Exception {
    this.initialize();
    String id = "AVq4gvXtF6rbBosITmqD";
    ServerDAO dao = factory.getServerDAO();
    
    Server server = dao.get(id, true);
    LOGGER.info(server.toString());
    
    this.destroy();
  }
  
  public void initialize() throws Exception {
    this.driver = new ElasticSearchDriver(new Properties());
    this.driver.initialize();
    
    this.factory = DAOFactory.create(driver);
  }
  
  public void destroy() throws Exception {
    this.driver.destroy();
  }
  
  public static void main(String[] args) throws Exception {
    //new ServerDAOImplTest().getAll();
    new ServerDAOImplTest().get();
  }

}
