package org.bionimbuzbox.dao;

import java.util.List;
import org.bionimbuzbox.model.Server;

public interface ServerDAO extends DAO<Server> {
	public List<Server> getAll(boolean cascade);
	public Server get(String id, boolean cascade);
}
