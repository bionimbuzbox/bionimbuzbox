package org.bionimbuzbox.dao.elasticsearch;

import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.ResourceDAO;
import org.bionimbuzbox.model.Resource;

public class ResourceDAOImpl extends ElasticSearchDAOImpl<Resource> implements ResourceDAO {

  private static final String ES_INDEX = "scheduling";
  private static final String ES_TYPE = "resource";
  
  public ResourceDAOImpl(DAODriver driver) {
    super(Resource.class, driver, ES_INDEX, ES_TYPE);
  }
  
}
