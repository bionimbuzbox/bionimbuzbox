package org.bionimbuzbox.dao.elasticsearch;

import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.WorkflowDAO;
import org.bionimbuzbox.model.Workflow;

public class WorkflowDAOImpl extends ElasticSearchDAOImpl<Workflow> implements WorkflowDAO {
	
  private static final String ES_INDEX = "scheduling";
  private static final String ES_TYPE = "workflow";
  
	public WorkflowDAOImpl(DAODriver driver) {
		super(Workflow.class, driver, ES_INDEX, ES_TYPE);
	}

}
