package org.bionimbuzbox.dao.elasticsearch;

import java.util.logging.Logger;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.ServerDAO;
import org.bionimbuzbox.model.Server;

public class ServerDAOImpl extends ElasticSearchDAOImpl<Server> implements ServerDAO {

	private static final Logger LOGGER = Logger.getLogger(ServerDAOImpl.class.getName());
	private static final String ES_INDEX = "provision";
	private static final String ES_TYPE = "server";
		
	public ServerDAOImpl(DAODriver driver) {
	  super(Server.class, driver, ES_INDEX, ES_TYPE);
	}
	
}
