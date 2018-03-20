package org.bionimbuzbox.provision.api;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.bionimbuzbox.dao.ClusterDAO;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.ProviderDAO;
import org.bionimbuzbox.model.Provider;

@Path("/")
public class ProviderAPI {
	
	@Inject
	private DAOFactory factory;
	private Logger LOGGER = Logger.getLogger(ProviderAPI.class.getName());

	@GET
	@Path("providers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Provider> listAll() {
		ProviderDAO dao = factory.getProviderDAO();
		return dao.getAll();
	}
	
	@GET
	@Path("provider/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Provider get(@PathParam("id") String id) {
		ProviderDAO dao = factory.getProviderDAO();
		return dao.get(id);
	}
	
	@POST
	@Path("provider")
	@Produces(MediaType.APPLICATION_JSON)
	public Provider save(Provider p) {
		LOGGER.info("Before:" + p.toString());
		ProviderDAO dao = factory.getProviderDAO();
		dao.save(p);
		LOGGER.info("After:" + p.toString());
		return p;
	}
	
	@DELETE
	@Path("provider/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remove(@PathParam("id") String id) {
		ProviderDAO dao = factory.getProviderDAO();
		dao.remove(id);
	}
}
