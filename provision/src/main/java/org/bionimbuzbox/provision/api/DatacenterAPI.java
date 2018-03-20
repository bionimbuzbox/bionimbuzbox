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
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.dao.DatacenterDAO;
import org.bionimbuzbox.model.Datacenter;

@Path("/")

public class DatacenterAPI {
	
	@Inject
	private DAOFactory factory;
	private static Logger LOGGER = Logger.getLogger(DatacenterAPI.class.getName());

	@GET
	@Path("datacenters")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Datacenter> listAll() {
		DatacenterDAO dao = factory.getDatacenterDAO();
		return dao.getAll();
	}
	
	@GET
	@Path("datacenter/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Datacenter get(@PathParam("id") String id) {
		DatacenterDAO dao = factory.getDatacenterDAO();
		return dao.get(id);
	}
	
	@POST
	@Path("datacenter")
	@Produces(MediaType.APPLICATION_JSON)
	public Datacenter save(Datacenter d) {
		DatacenterDAO dao = factory.getDatacenterDAO();
		dao.save(d);
		return d;
	}
	
	@DELETE
	@Path("datacenter/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remove(@PathParam("id") String id) {
		DatacenterDAO dao = factory.getDatacenterDAO();
		dao.remove(id);
	}
}
