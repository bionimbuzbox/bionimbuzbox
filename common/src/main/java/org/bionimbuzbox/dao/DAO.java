package org.bionimbuzbox.dao;

import java.util.List;
import org.bionimbuzbox.model.Model;

public interface DAO <T extends Model> {
  public T get(String id);
  public void save(T type);
  public List<T> getAll();
  public void remove(String id);
}
