package org.bionimbuzbox.model;

import org.bionimbuzbox.dao.WorkflowDAO;

abstract public class PersistentWorkflow extends Workflow implements WorkflowDAO {
  
  private static final long serialVersionUID = -4386588860427312827L;

  abstract public void configure(WorkflowDAO dao);

}
