package org.bionimbuzbox.model;

import org.bionimbuzbox.dao.ResourceDAO;

abstract public class PersistentResource extends Resource implements ResourceDAO {

  private static final long serialVersionUID = 2179187348313809100L;

  abstract public void configure(ResourceDAO dao);
  
}
