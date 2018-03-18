package org.bionimbuzbox.dao;

import java.util.List;
import org.bionimbuzbox.model.Datacenter;

public interface DatacenterDAO extends DAO<Datacenter> {
  public List<Datacenter> getAll(boolean cascade);
  public Datacenter get(String id, boolean cascade);
}
