package org.bionimbuzbox.dao.elasticsearch;

import java.util.logging.Logger;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.ProviderDAO;
import org.bionimbuzbox.model.Provider;

public class ProviderDAOImpl extends ElasticSearchDAOImpl<Provider> implements ProviderDAO {

	private static final Logger LOGGER = Logger.getLogger(ProviderDAOImpl.class.getName());
	private static final String ES_INDEX = "provision";
	private static final String ES_TYPE = "provider";
		
	public ProviderDAOImpl(DAODriver driver) {
	  super(Provider.class, driver, ES_INDEX, ES_TYPE);
	}

}
