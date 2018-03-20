package org.bionimbuzbox.communication.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.bionimbuzbox.dao.DAOFactory;
import org.bionimbuzbox.model.Event;
import org.bionimbuzbox.model.EventHandler;


@Path("/")
public class CommunicationAPI {
	
	@Inject
	private DAOFactory factory;
	private static Logger LOGGER = Logger.getLogger(CommunicationAPI.class.getName());

	@GET
	@Path("event")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Event> listEvents() {
		LOGGER.info("Getting all Events ...");
		return new ArrayList<Event>();
	}
	
	@GET
	@Path("event-handler")
	@Produces(MediaType.APPLICATION_JSON)
	public List<EventHandler> listEventHandlers() {
		LOGGER.info("Getting all EventHandlers ...");
		return new ArrayList<EventHandler>();
	}
	
	@GET
	@Path("event/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Event getEvent(@PathParam("id") String id) {
		return this.factory.getEventDAO().get(id);
	}
	
	@GET
	@Path("event-handler/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public EventHandler getEventHandler(@PathParam("id") String id) {
		return this.factory.getEventHandlerDAO().get(id);
	}
	/*
	@POST
	@Path("cluster")
	@Produces(MediaType.APPLICATION_JSON)
	public Cluster save(Cluster c) {
		LOGGER.info(c.toString());
		c = (Cluster) this.db.save(c);
		return c;
	}
	
	@DELETE
	@Path("cluster/{id : \\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public void remove(@PathParam("id") long id) {
		Cluster c = this.db.findByID(Cluster.class, id);
		this.db.remove(c);
	}
	*/
}
