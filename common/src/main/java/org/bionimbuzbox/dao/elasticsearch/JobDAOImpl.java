package org.bionimbuzbox.dao.elasticsearch;

import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.JobDAO;
import org.bionimbuzbox.model.Job;

public class JobDAOImpl extends ElasticSearchDAOImpl<Job> implements JobDAO {
	
  private static final String ES_INDEX = "scheduling";
  private static final String ES_TYPE = "job";
  
	public JobDAOImpl(DAODriver driver) {
		super(Job.class, driver, ES_INDEX, ES_TYPE);
	}

}
