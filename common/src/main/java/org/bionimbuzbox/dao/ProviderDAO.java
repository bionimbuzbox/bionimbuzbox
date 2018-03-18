package org.bionimbuzbox.dao;

import java.util.List;
import org.bionimbuzbox.model.Provider;

public interface ProviderDAO extends DAO<Provider> {
  public List<Provider> getAll(boolean cascade);
  public Provider get(String id, boolean cascade);
}
