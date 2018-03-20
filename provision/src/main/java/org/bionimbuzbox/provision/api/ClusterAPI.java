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
import org.bionimbuzbox.ca.CA;
import org.bionimbuzbox.ca.RootCertificate;
import org.bionimbuzbox.dao.ClusterDAO;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.model.Cluster;

@Path("/")

public class ClusterAPI {
	
	@Inject
	private DAOFactory factory;
	private static Logger LOGGER = Logger.getLogger(ClusterAPI.class.getName());

	@GET
	@Path("clusters")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Cluster> listAll() {
		ClusterDAO dao = factory.getClusterDAO();
		return dao.getAll();
	}
	
	@GET
	@Path("cluster/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Cluster get(@PathParam("id") String id) {
		ClusterDAO dao = factory.getClusterDAO();
		return dao.get(id);
	}
	
	@POST
	@Path("cluster")
	@Produces(MediaType.APPLICATION_JSON)
	public Cluster save(Cluster cluster) {
		ClusterDAO clusterDAO = factory.getClusterDAO();
		
		if (!cluster.getMeta().containsKey("ssl-ca-cert") || 
		    !cluster.getMeta().containsKey("ssl-ca-key")) {
		  addCACertificate(cluster);
		}
		
		clusterDAO.save(cluster);
		return cluster;
	}
	
	@DELETE
	@Path("cluster/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remove(@PathParam("id") String id) {
		ClusterDAO dao = factory.getClusterDAO();
		dao.remove(id);
	}
	
	public void addCACertificate(Cluster cluster) {
	  RootCertificate ca = CA.createSelfSignedCertificate(CA.dn("CN=BioNimbuzBoxCA")).build();
    cluster.addMetaInfo("ssl-ca-cert", ca.print());
    cluster.addMetaInfo("ssl-ca-key", ca.printKey());
	}
}
