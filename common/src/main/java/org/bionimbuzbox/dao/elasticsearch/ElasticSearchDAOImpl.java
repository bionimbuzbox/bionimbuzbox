package org.bionimbuzbox.dao.elasticsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bionimbuzbox.dao.DAO;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.model.Cluster;
import org.bionimbuzbox.model.Datacenter;
import org.bionimbuzbox.model.Model;
import org.bionimbuzbox.model.Provider;
import org.bionimbuzbox.model.Server;
import org.elasticsearch.client.transport.TransportClient;

public class ElasticSearchDAOImpl<T extends Model> implements DAO<T> {

  private static final Logger LOGGER = Logger.getLogger(ElasticSearchDAOImpl.class.getName());
  private final String ES_INDEX;
  private final String ES_TYPE;
  private final Class<T> CLAZZ;
  private final ElasticSearchDriver DRIVER;
  
  private final TransportClient CLIENT;
  
  public ElasticSearchDAOImpl(Class<T> clazz, DAODriver driver, String index, String type) {
    DRIVER = ((ElasticSearchDriver) driver);
    CLIENT = DRIVER.getTransportClient();
    
    CLAZZ = clazz;
    ES_INDEX = index;
    ES_TYPE = type;
  }

  @Override
  public T get(String id) {    
    return ElasticSearchDAOHelper.get(id, CLAZZ, CLIENT, ES_INDEX, ES_TYPE);
  }
  
  public T get(String id, boolean cascade) {
    if (!cascade) {
      return get(id);
    }
    
    List<T> list = getAll(true);
    
    return list.stream()
      .filter(o -> o.getId().equals(id))
      .findFirst()
      .orElse(null);
  }

  @Override
  public List<T> getAll() {    
    return ElasticSearchDAOHelper.getAll(CLAZZ, CLIENT, ES_INDEX, ES_TYPE);
  }
  
  @SuppressWarnings("unchecked")
  public List<T> getAll(boolean cascade) {
    if (!cascade) {
      return getAll();
    }
    
    DAOFactory factory = DAOFactory.create(DRIVER);
    
    List<Provider> providers = factory.getProviderDAO().getAll();
    
    List<Datacenter> datacenters = factory.getDatacenterDAO().getAll();
    
    List<Cluster> clusters = factory.getClusterDAO().getAll();
    
    List<Server> servers = factory.getServerDAO().getAll();
      
      for (Provider p : providers) {
        List<Datacenter> d = datacenters.stream()
            .filter(o -> o.getProvider().getId().equals(p.getId()))
            .collect(Collectors.toList());
        p.setDatacenters(d);
      }
      
      for (Datacenter d : datacenters) {
        List<Cluster> c = clusters.stream()
            .filter(o -> o.getDatacenter().getId().equals(d.getId()))
            .collect(Collectors.toList());
        
        Provider p = providers.stream()
            .filter(o -> o.getId().equals(d.getProvider().getId()))
            .findFirst()
            .orElse(null);
        
        d.setProvider(p);
        d.setClusters(c);
      }
      
      for (Cluster c : clusters) {
        List<Server> s = servers.stream()
            .filter(o -> o.getCluster().getId().equals(c.getId()))
            .collect(Collectors.toList());
        
        Datacenter d = datacenters.stream()
            .filter(o -> o.getId().equals(c.getDatacenter().getId()))
            .findFirst()
            .orElse(null);
        
        c.setDatacenter(d);
        c.setServers(s);
      }
      
      for (Server s : servers) {
        Cluster c = clusters.stream()
            .filter(o -> o.getId().equals(s.getCluster().getId()))
            .findFirst()
            .orElse(null);
        
        s.setCluster(c);
      }
      
      if (CLAZZ.isAssignableFrom(Provider.class)) {
        return (List<T>) providers;
      }
      
      if (CLAZZ.isAssignableFrom(Datacenter.class)) {
        return (List<T>) datacenters;
      }
      
      if (CLAZZ.isAssignableFrom(Cluster.class)) {
        return (List<T>) clusters;
      }
      
      if (CLAZZ.isAssignableFrom(Server.class)) {
        return (List<T>) servers;
      }
      
      return new ArrayList<T>();
  }
  
  @Override
  public void save(T type) {
    ElasticSearchDAOHelper.save(type, CLIENT, ES_INDEX, ES_TYPE);
  }
  
  @Override
  public void remove(String id) {
    ElasticSearchDAOHelper.remove(id, CLIENT, ES_INDEX, ES_TYPE);
  }

}
