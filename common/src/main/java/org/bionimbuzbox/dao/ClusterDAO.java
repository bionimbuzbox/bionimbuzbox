package org.bionimbuzbox.dao;

import java.util.List;
import org.bionimbuzbox.model.Cluster;

public interface ClusterDAO extends DAO<Cluster> {
  public List<Cluster> getAll(boolean cascade);
  public Cluster get(String id, boolean cascade);
}
