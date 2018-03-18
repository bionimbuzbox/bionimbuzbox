package org.bionimbuzbox.model;

import org.bionimbuzbox.dao.JobDAO;

abstract public class PersistentJob extends Job implements JobDAO {
  
  private static final long serialVersionUID = -2031336375701769785L;

  abstract public void configure(JobDAO dao);
  
}
