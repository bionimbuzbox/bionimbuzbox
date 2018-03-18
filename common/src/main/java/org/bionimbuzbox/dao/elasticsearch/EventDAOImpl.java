package org.bionimbuzbox.dao.elasticsearch;

import java.util.logging.Logger;
import org.bionimbuzbox.dao.DAODriver;
import org.bionimbuzbox.dao.EventDAO;
import org.bionimbuzbox.model.Event;

public class EventDAOImpl extends ElasticSearchDAOImpl<Event> implements EventDAO {
	private static final Logger LOGGER = Logger.getLogger(EventDAOImpl.class.getName());
	private static final String ES_INDEX = "event";
	private static final String ES_TYPE = "event";
		
	public EventDAOImpl(DAODriver driver) {
		super(Event.class, driver, ES_INDEX, ES_TYPE);
	}
	 
}
