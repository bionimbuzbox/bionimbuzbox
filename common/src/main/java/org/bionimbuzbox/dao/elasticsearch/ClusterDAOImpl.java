package org.bionimbuzbox.dao.elasticsearch;

import java.util.logging.Logger;
import org.bionimbuzbox.dao.ClusterDAO;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.model.Cluster;

public class ClusterDAOImpl extends ElasticSearchDAOImpl<Cluster> implements ClusterDAO {
	
	private static final Logger LOGGER = Logger.getLogger(ClusterDAOImpl.class.getName());
	private static final String ES_INDEX = "provision";
	private static final String ES_TYPE = "cluster";
		
	public ClusterDAOImpl(DAODriver driver) {
    super(Cluster.class, driver, ES_INDEX, ES_TYPE);
	}
}
