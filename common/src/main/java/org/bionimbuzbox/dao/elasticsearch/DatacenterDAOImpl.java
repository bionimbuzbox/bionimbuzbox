package org.bionimbuzbox.dao.elasticsearch;

import java.util.logging.Logger;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.DatacenterDAO;
import org.bionimbuzbox.model.Datacenter;

public class DatacenterDAOImpl extends ElasticSearchDAOImpl<Datacenter> implements DatacenterDAO {

	private static final Logger LOGGER = Logger.getLogger(DatacenterDAOImpl.class.getName());
	private static final String ES_INDEX = "provision";
	private static final String ES_TYPE = "datacenter";
		
	public DatacenterDAOImpl(DAODriver driver) {
		super(Datacenter.class, driver, ES_INDEX, ES_TYPE);
	}

}
